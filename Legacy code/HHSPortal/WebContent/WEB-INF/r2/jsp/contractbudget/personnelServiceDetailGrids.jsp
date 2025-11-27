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
	refreshPSDetailNonGridData($subBudgetId);
	$('#detailedView'+$subBudgetId).attr('disabled',true);
	//Start: Added in Defect-8470
	$('#detailedView'+$subBudgetId).removeAttr('style');
	$('#summaryView'+$subBudgetId).attr('disabled',false);
	//End: Added in Defect-8470
	$('#summaryView'+$subBudgetId).attr('style','background-color: white !important;color: grey !important;');
});

/**
* This function called to refresh fringe grid, rate column.
**/
function refreshFringGridDetailHeader(subBudgetIdVal){
	pageGreyOut();
	var cellValue = $('#val4'+subBudgetIdVal).html();
	if(cellValue != null)
	{
		cellValue = cellValue.replace('(','').replace(')','');
		var fringeObj = $('#table_fringeBenifitsGrid-'+subBudgetIdVal+'>tbody>tr:eq(1)>td:eq(2)');
		fringeObj.removeClass();
		fringeObj.removeAttr('style');
		fringeObj.css({"text-align": "center","font-weight":"bold"});
		fringeObj.html(cellValue);
		fringeObj.attr('title',cellValue);
	}
	removePageGreyOut();
}

/**
* This function called to refresh non-grid information.
**/
function refreshPSDetailNonGridData(subBudgetIdVal){
	pageGreyOut();
	var v_parameter = '&nextAction=getPersonnelServicesDetailData&subBudgetId='+subBudgetIdVal;
	var urlAppender = $("#getCallBackContractBudgetData").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
				$("#val8"+e['SubBudgetId']).html(e['DetailedScreenMessage']);
				$("#val1"+e['SubBudgetId']).html(e['CitySalaryAndFringeAmount']).jqGridCurrency();
				$("#val2"+e['SubBudgetId']).html(e['CitySalaryAmount']).jqGridCurrency();
				$("#val3"+e['SubBudgetId']).html(e['CityFringeAmount']).jqGridCurrency();
				$("#val4"+e['SubBudgetId']).html(" ("+((e['FringePercentage'] == 0)?'0.00':e['FringePercentage'])+"%)");
				$("#val6"+e['SubBudgetId']).html(e['Position']);
				$("#val7"+e['SubBudgetId']).html(e['totalCityFte']);
				removePageGreyOut();
			return false;
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
}

/**
* This function called On loadcomplete to calculate %city funded of hourly grid
**/
function refreshPSDetailHourlyPositionHeader(subBudgetIdVal, jsonObj){
	pageGreyOut();
	var tableIdObj = '#table_hourlyPositionDetailsGrid-';
	var tbodyObj = '>tbody>tr:eq(1)>td:eq(';
	var fyBudgetHeaderObj = $(tableIdObj+subBudgetIdVal+tbodyObj+'6)');
	var cityFundedHeaderObj = $(tableIdObj+subBudgetIdVal+tbodyObj+'7)');
	var totalRateNHour = null;
	for(var i=0; i<jsonObj.rows.length; i++){
		if(totalRateNHour == null){
			totalRateNHour =  new Big(jsonObj.rows[i].rate).times(jsonObj.rows[i].hourPerYear);
		}
		else{
			totalRateNHour = new Big(totalRateNHour).plus(new Big(jsonObj.rows[i].rate).times(jsonObj.rows[i].hourPerYear));
		}
	}
	var cityFundVal = null;
	if(totalRateNHour != null){
		//Start : Update for Defect-8497
		try{
			cityFundVal = new Big(fyBudgetHeaderObj.html().replace('$','').replaceAll(',','')).div(new Big((totalRateNHour==null)?0:totalRateNHour)).times(100).toFixed(2) + '%';
		}catch(err){
			cityFundVal = "0.00%";
		}
		//End : Update for Defect-8497
		if(cityFundVal.indexOf('NaN%') === 0 || cityFundVal.indexOf('Infinity%') === 0){
			cityFundVal = "0.00%";
		}
	}
	else{
		cityFundVal = "0.00%";
	}
	cityFundedHeaderObj.html(cityFundVal);
	cityFundedHeaderObj.attr('title',cityFundVal);
	removePageGreyOut();
}

</script>
<style>
.iconPosition{
	background-position: 4px 22px !important;
	line-height: 18px;
}
</style>

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
<p id="val8${subBudgetId}" class="infoMessage iconPosition" style="display:block">Loading</p>
<!-- End: Updated for 8502 -->


<div class='clear'>&nbsp;</div>
<!-- Detailed Grid -->
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetSalariedDetailed.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='salariedDetailLoadGridData' id='loadGridData' escapeXml='false'>
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
<%--Added setTooltipsOnColumnHeader for Defect-8500 --%>
<%-- Added numberCommaFormatTemplate and changeCurrency for Defect-8501--%>
<jq:grid id="salariedPositionDetailsGrid-${subBudgetId}" 
        isReadOnly="true"
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
		 subGridUrl="${salariedDetailLoadGridData}"
	     cellUrl="${salariedEmployeeOperationGrid}"
	     editUrl="${salariedEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="budgetAmount,annualSalary,%"
         isPagination="true"
	     rowsPerPage="10"
         isSubGrid="true"
         nonEditColumnName="invoicedAmount,remainingAmount"   
         positiveCurrency="budgetAmount,annualSalary,hourPerYear" 
   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
   	     callbackFunction="refreshPSDetailNonGridData('${subBudgetId}');"
   	     dropDownData="${personnelServiceMasterData}"
   	     exportFileName="PS_DETAIL_SALARIED_"
   	     autoWidth="false"
   	     callbackFnAfterLoadGrid="$('#table_salariedPositionDetailsGrid-${subBudgetId}>tbody>tr:eq(1)>td:eq(5)').changeCurrency();setTooltipsOnColumnHeader($('#table_salariedPositionDetailsGrid-${subBudgetId}'),'5','Total Hours Worked for Organization. Fulltime employee is 2,087 hours.');setTooltipsOnColumnHeader($('#table_salariedPositionDetailsGrid-${subBudgetId}'),'6','City Funded Amount');"
/>

<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetHourlyDetailed.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='HourlyDetailLoadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyPositionDetailsGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="contractBudgetHourlyDetailed.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='salariedEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyPositionDetailsGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetHourlyDetailed.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetHourlyDetailed.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetHourlyDetailed.grid")%></c:set>
<br>
<!-- Updated Rate column Template to currencyTemplate for Defect 8443 -->
<%--Added setTooltipsOnColumnHeader for Defect-8500 --%>
<%-- Added numberCommaFormatTemplate and changeCurrency for Defect-8501--%>
 <jq:grid id="hourlyPositionDetailsGrid-${subBudgetId}" 
         isReadOnly="true"
         subGridRowNumbers="31"
         autoWidth="false"
         gridColNames="${gridColNames}" 
	     gridColProp="{name:'empPosition',width:'200'},{name:'internalTitle',width:'100'},{name:'rate',width:'150',template:currencyTemplate},{name:'hourPerYear',width:'100',template:numberCommaFormatTemplate},{name:'budgetAmount',width:'150',template:currencyTemplate},{name:'invoicedAmount',width:'100',template:percentageTemplate}" 
	     subGridColProp="{editable:true, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
                  {editable:true, editrules:{required:false,text:true},editoptions:{maxlength:7}},
                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                  {editable:false, editrules:{required:true,number:true}}"
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${HourlyDetailLoadGridData}"
	     cellUrl="${salariedEmployeeOperationGrid}"
	     editUrl="${salariedEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="budgetAmount,budgetAmount,%"
         isPagination="true"
	     rowsPerPage="10"
         isSubGrid="true"
         nonEditColumnName="invoicedAmount,remainingAmount"   
         positiveCurrency="budgetAmount,annualSalary,hourPerYear" 
   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
   	     callbackFunction="refreshPSDetailNonGridData('${subBudgetId}');"
   	     callbackFnAfterLoadGrid="$('#table_hourlyPositionDetailsGrid-${subBudgetId}>tbody>tr:eq(1)>td:eq(5)').changeCurrency();refreshPSDetailHourlyPositionHeader('${subBudgetId}',parentTotal);setTooltipsOnColumnHeader($('#table_hourlyPositionDetailsGrid-${subBudgetId}'),'4','Standard Hourly Wage');setTooltipsOnColumnHeader($('#table_hourlyPositionDetailsGrid-${subBudgetId}'),'5','Total Hours Worked for Organization. Fulltime employee is 2,087 hours.');setTooltipsOnColumnHeader($('#table_hourlyPositionDetailsGrid-${subBudgetId}'),'6','City Funded Amount');"
   	     dropDownData="${personnelServiceMasterData}"
   	     exportFileName="PS_DETAIL_HOURLY_"
/>

<br>

<portlet:resourceURL var='SubGridHeaderRow3' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetFringeBenifitsDetail.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='FringeDetailLoadGridData' id='loadGridData' escapeXml='false'>
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
         isReadOnly="true"
         gridColNames="${gridColNames3}" 
	     gridColProp="{name:'fringeBenifits',width:'390'},{name:'rate',template:percentageTemplate},{name:'budgetAmount',template:currencyTemplate}" 
	     subGridColProp="${subGridColProp3}" 
		 gridUrl="${SubGridHeaderRow3}"
		 subGridUrl="${FringeDetailLoadGridData}"
	     cellUrl="${fringeBenifitsOperationGrid}"
	     editUrl="${fringeBenifitsOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="false"
	     rowsPerPage="14"
         isSubGrid="true"
         positiveCurrency="budgetAmount" 
		 nonEditColumnName="invoicedAmount,remainingAmount"    
   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
   	     callbackFunction="refreshPSDetailNonGridData('${subBudgetId}');"
   	     callbackFnAfterLoadGrid="refreshFringGridDetailHeader('${subBudgetId}');setTooltipsOnColumnHeader($('#table_fringeBenifitsGrid-${subBudgetId}'),'3','Total City Funded Amount');"
   	     exportFileName="PS_DETAIL_FRINGE_"
/>
