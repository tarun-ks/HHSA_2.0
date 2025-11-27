<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%--R7 Start: --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%--R7 End --%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractBudgets.js"></script>
<portlet:defineObjects />

<script type="text/javascript">
$(document).ready(function() {
	$(".budgetSummary #summaryAmount${subBudgetId}").each(function(e) {
		$(this).jqGridCurrency();
	});
	
	//Personnel Services Currency Formatter
	var subBudgetID = ${subBudgetId};
	if($('#1_'+subBudgetID).html() != null)
	{
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
	}
	//Operation and Support Currency Formatter
	if($('#2_'+subBudgetID).html() != null)
	{
		$("#fyBudgetOTPS"+subBudgetID).jqGridCurrency();
		$("#ytdInvAmtOTPS" + subBudgetID).jqGridCurrency();
	}
	//Contracted Services Currency Formatter
	if($('#6_'+subBudgetID).html() != null)
	{
		$("#totCS"+subBudgetID).jqGridCurrency();
		$("#ytdIA"+subBudgetID).jqGridCurrency();
	}
});
</script>
<style type="text/css">
	objHidden {display:none}
</style>

<div style='margin: 20px' >

	<c:set var="tabToShowList" value="${tabToShowList}"/>
	
	<div class="budgetSummary" style='padding:0' >	
		<portlet:defineObjects />
		<c:set var="changeId" value="${subBudgetId}"></c:set>
		<h3>Budget Summary</h3>
		<p></p>
		<table width="100%" cellspacing='0' cellpadding='0'>				
				<tr>
					<th colspan='2' class='alignCenter'>Line Item</th>
					<th class='alignCenter'>FY Budget</th>
					<th class='alignCenter'>YTD Invoiced Amount</th>
					<th class='alignCenter'>Remaining Amount</th>
				</tr>
				<tr>
					<td width='3%' class='togglePlaceholder' id="togglerMain${changeId}" onclick="showme('taggingMain${changeId}', this.id);"><span>-</span></td>
					<td width='44%' class='bold'>Total City Funded Budget</td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalCityFundedBudget.approvedBudget}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalCityFundedBudget.ytdInvoicedAmount}</label></label></td>
					<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalCityFundedBudget.remainingAmount}</label></label></td>
				</tr>
		</table>
		
		<div id='taggingMain${changeId}' >
			
			<%-- "Total Direct Costs" table Starts --%>			
				<table width="92%" cellspacing='0' cellpadding='0'>				
					<tr>
						<td width='40%' class='noBdr'><h3>Total Direct Costs</h3></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalDirectsCosts.approvedBudget}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalDirectsCosts.ytdInvoicedAmount}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalDirectsCosts.remainingAmount}</label></label></td>
					</tr>
				</table>
			<%-- "Total Direct Costs" table Ends Here --%>		
				
				
			<%-- "Total Salary and Fringe" Table Starts --%>
				<table width="92%" cellspacing='0' cellpadding='0'>					
					<tr>
						<td width='3%' class='togglePlaceholder' id="toggler${changeId}" onclick="showme('tagging${changeId}', this.id);"><span>-</span></td>
						<td width='37%' class='bold'>Total Salary and Fringe</td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalaryAndFringesAmount.approvedBudget}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalaryAndFringesAmount.ytdInvoicedAmount}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalaryAndFringesAmount.remainingAmount}</label></label></td>
					</tr>
						<tbody id="tagging${changeId}">
						<tr >
							<td>&nbsp;</td>
							<td>Total Salary</td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalary.approvedBudget}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalary.ytdInvoicedAmount}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalSalary.remainingAmount}</label></label></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>Total Fringe</td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalFringes.approvedBudget}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalFringes.ytdInvoicedAmount}</label></label></td>
							<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalFringes.remainingAmount}</label></label></td>	
						</tr>
					</tbody>
				</table>
			<%-- "Total Salary and Fringe" Table Ends Here --%>
				
			<%-- "Total OTPS" Table starts --%>
				<table width="92%" cellspacing='0' cellpadding='0' class='summaryWrapper2'>				
						<tr>
							<td width='3%' class='togglePlaceholder' id="toggler2${changeId}" onclick="showme('tagging2${changeId}', this.id);"><span>-</span></td>
							<td width='37%' class='bold'>Total OTPS</td>
							<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalOTPSAmount.approvedBudget}</label></label></td>
							<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalOTPSAmount.ytdInvoicedAmount}</label></label></td>
							<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalOTPSAmount.remainingAmount}</label></label></td>
						</tr>
						<tbody id='tagging2${changeId}'>
							<tr>
								<td>&nbsp;</td>
								<td>Operations, Support and Equipment</td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.operationsSupportAndEquipmentAmount.approvedBudget}</label></label></td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.operationsSupportAndEquipmentAmount.ytdInvoicedAmount}</label></label></td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.operationsSupportAndEquipmentAmount.remainingAmount}</label></label></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>Utilities</td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.utilitiesAmount.approvedBudget}</label></label></td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.utilitiesAmount.ytdInvoicedAmount}</label></label></td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.utilitiesAmount.remainingAmount}</label></label></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>Professional Services</td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.professionalServicesAmount.approvedBudget}</label></label></td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.professionalServicesAmount.ytdInvoicedAmount}</label></label></td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.professionalServicesAmount.remainingAmount}</label></label></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>Rent & Occupancy</td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.rentAndOccupancyAmount.approvedBudget}</label></label></td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.rentAndOccupancyAmount.ytdInvoicedAmount}</label></label></td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.rentAndOccupancyAmount.remainingAmount}</label></label></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>Contracted Services</td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.contractedServicesAmount.approvedBudget}</label></label></td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.contractedServicesAmount.ytdInvoicedAmount}</label></label></td>
								<td class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.contractedServicesAmount.remainingAmount}</label></label></td>
							</tr>
						</tbody>
				</table>
			<%-- "Total OTPS" Table Ends here --%>
				
			<%-- "Total Rate Based" table Starts --%>			
				<table width="92%" cellspacing='0' cellpadding='0' class='summaryWrapper2'>				
					<tr>
						<td width='40%' class='bold'>Total Rate Based</td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalRateBasedAmount.approvedBudget}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalRateBasedAmount.ytdInvoicedAmount}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalRateBasedAmount.remainingAmount}</label></label></td>
					</tr>
				</table>
			<%-- "Total Rate Based" table Ends Here --%>	
	
			<%-- "Total Milestone Based" table Starts --%>			
				<table width="92%" cellspacing='0' cellpadding='0' class='summaryWrapper2'>				
					<tr>
						<td width='40%' class='bold'>Total Milestone Based</td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalMilestoneBasedAmount.approvedBudget}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalMilestoneBasedAmount.ytdInvoicedAmount}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalMilestoneBasedAmount.remainingAmount}</label></label></td>
					</tr>
				</table>
			<%-- "Total Milestone Based" table Ends Here --%>	
	
			<%-- "Unallocated Funds" table Starts --%>			
				<table width="92%" cellspacing='0' cellpadding='0' class=''>				
					<tr>
						<td width='40%' class='bold'>Unallocated Funds</td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.unallocatedFunds.approvedBudget}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.unallocatedFunds.ytdInvoicedAmount}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.unallocatedFunds.remainingAmount}</label></label></td>
					</tr>
				</table>
			<%-- "Unallocated Funds" table Ends Here --%>
	
			<h3 style='margin-left:8%;'>Total Indirect Costs</h3>
			<p style='margin-left:7%;'>
				&nbsp;&nbsp;<b>Indirect Rate - City Funded</b> &nbsp;${asIndirectRate} %
			</p>
			
			<%-- "Total Indirect Costs" table Starts --%>			
				<table width="92%" cellspacing='0' cellpadding='0'>				
					<tr>
						<td width='40%' class='bold'>Total Indirect Costs</td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalIndirectCosts.approvedBudget}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalIndirectCosts.ytdInvoicedAmount}</label></label></td>
						<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalIndirectCosts.remainingAmount}</label></label></td>
					</tr>
				</table>
			<%-- "Total Indirect Costs" table Ends Here --%>	
		
		</div>
		
		<hr>
				
		<table width="100%" cellspacing='0' cellpadding='0'>				
			<tr>
				<td width='3%'>&nbsp;</td>
				<td width='45%' class='bold'>Total Program Income
					<div>(Excluded from City Funded Budget; Not Invoiced)</div>
				</td>
				<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramIncome.approvedBudget}</label></label></td>
				<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramIncome.ytdInvoicedAmount}</label></label></td>
				<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramIncome.remainingAmount}</label></label></td>
			</tr>
	    </table>
		
		<table width="100%" cellspacing='0' cellpadding='0'>				
			<tr>
				<td width='48%' class='bold'>Total Program Budget  
					<div>(City Funded Budget + Program Income)</div>
				</td>
				<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramBudget.approvedBudget}</label></label></td>
				<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramBudget.ytdInvoicedAmount}</label></label></td>
				<td width='13%' class='right'><label><label id="summaryAmount${subBudgetId}">${budgetSummary.totalProgramBudget.remainingAmount}</label></label></td>
			</tr>
	    </table>
	    <div>&nbsp;</div><hr><div>&nbsp;</div>
	</div>

	<c:if test="${fn:contains(tabToShowList, '@###@1_')}">
		<div id ='1_${subBudgetId}'>
			<h3>Personnel Services</h3>
			<div class="formcontainer paymentFormWrapper widthFull">
			<c:choose>
			<%-- Start: Added for 8458 --%>
				<c:when test="${existingBudget eq 0}">
				<div class="row">
					<span class="label">City Salary &amp; Fringe:</span>
				    <span class="formfield">
				    	<span class='lftAmount'><label id="val1${subBudgetId}">${personnelServiceData.totalSalaryAndFringeAmount}</label></span>
				    </span>
				</div>
				<div class="row">
					<span class="label">City Salary:</span>
					<span class="formfield">
						<span class='lftAmount'><label id="val2${subBudgetId}">${personnelServiceData.totalSalaryAmount}</label></span>
					</span>
				</div>  
				<div class="row">
				  <span class="label">City Fringe:</span>
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
				<div class="row">
				  <span class="label">Total Positions:</span>
				  <span class="formfield">
				  	<span class='lftAmount'><label id="val4${subBudgetId}">${personnelServiceData.totalPositions}</label></span>
				  </span>  
				</div>
				</c:when>
				<%-- End: Added for 8458 --%>
				<c:otherwise>
				<div class="row">
					<span class="label">Total Salary &amp; Fringe:</span>
				    <span class="formfield">
				    	<span class='lftAmount'><label id="val1${subBudgetId}">${personnelServiceData.totalSalaryAndFringeAmount}</label></span>
				    </span>
				</div>
				<div class="row">
					<span class="label">Total Salary:</span>
					<span class="formfield">
						<span class='lftAmount'><label id="val2${subBudgetId}">${personnelServiceData.totalSalaryAmount}</label></span>
					</span>
				</div>  
				<div class="row">
				  <span class="label">Total Fringe:</span>
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
				</c:otherwise>
			</c:choose>
			</div>
		
			<div class='clear'>&nbsp;</div>
		
			<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractBudgetSalariedEmployee.grid"/>
			</portlet:resourceURL>
			<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="salariedEmployeeGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
				<portlet:param name="gridLabel" value="contractBudgetSalariedEmployee.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<portlet:resourceURL var='salariedEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="salariedEmployeeGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<c:choose>
				<c:when test="${existingBudget eq 0}">
				<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetSalariedSummary.grid")%></c:set>
				<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetSalariedSummary.grid")%></c:set>
				<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetSalariedSummary.grid")%></c:set>
				</c:when>
				<c:otherwise>
				<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetSalariedEmployee.grid")%></c:set>
				<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetSalariedEmployee.grid")%></c:set>
				<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetSalariedEmployee.grid")%></c:set>
				</c:otherwise>
			</c:choose>
			
			<jq:grid id="salariedEmployeeGrid-${subBudgetId}" 
			        isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="{editable:true, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
			                  {editable:true, editrules:{isMandatoryField},editoptions:{dataInit: function (elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMax:'99999.99'});}, 100);}}},
			                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
			                  {editable:false, editrules:{required:true,number:true}},
			                  {editable:false, editrules:{required:true,number:true}}"
					 gridUrl="${SubGridHeaderRow}"
					 subGridUrl="${loadGridData}"
				     cellUrl="${salariedEmployeeOperationGrid}"
				     editUrl="${salariedEmployeeOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="budgetAmount,-invoicedAmount"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         nonEditColumnName="invoicedAmount,remainingAmount"   
			         positiveCurrency="budgetAmount" 
			   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
			   	     dropDownData="${personnelServiceMasterData}"
			   	     isExpandOnLoad="true"
			/>
		
			<portlet:resourceURL var='SubGridHeaderRow1' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractBudgetHourlyEmployee.grid"/>
			</portlet:resourceURL>
			<portlet:resourceURL var='loadGridData1' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="hourlyEmployeeGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
				<portlet:param name="gridLabel" value="contractBudgetHourlyEmployee.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<portlet:resourceURL var='hourlyEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="hourlyEmployeeGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL> 
			<c:choose>
				<c:when test="${existingBudget eq 0}">
				<c:set var="gridColNames1"><%=HHSUtil.getHeader("contractBudgetHourlySummary.grid")%></c:set> 
				<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("contractBudgetHourlySummary.grid")%></c:set> 
				<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("contractBudgetHourlySummary.grid")%></c:set>
				</c:when>
				<c:otherwise>
				<c:set var="gridColNames1"><%=HHSUtil.getHeader("contractBudgetHourlyEmployee.grid")%></c:set> 
				<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("contractBudgetHourlyEmployee.grid")%></c:set> 
				<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("contractBudgetHourlyEmployee.grid")%></c:set>
				</c:otherwise>
			</c:choose>
			<br>
			
			<jq:grid id="hourlyEmployeeGrid-${subBudgetId}" 
			        isReadOnly="true"
			         gridColNames="${gridColNames1}" 
				     gridColProp="${gridColProp1}" 
				     subGridColProp="{editable:true, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
			                  {editable:true, editrules:{isMandatoryField},editoptions:{dataInit: function (elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMax:'99999.99'});}, 100);}}},
			                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
			                  {editable:false, editrules:{required:true,number:true}},
			                  {editable:false, editrules:{required:true,number:true}}"
					 gridUrl="${SubGridHeaderRow1}"
					 subGridUrl="${loadGridData1}"
				     cellUrl="${hourlyEmployeeOperationGrid}"
				     editUrl="${hourlyEmployeeOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="budgetAmount,-invoicedAmount"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         nonEditColumnName="invoicedAmount,remainingAmount" 
			         positiveCurrency="budgetAmount" 
			   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
			   	     dropDownData="${personnelServiceMasterData}"
			   	     isExpandOnLoad="true"
			/>
			<!-- R6 - Hiding the seasonal grid for new budget start -->
			<c:if test="${existingBudget eq 1}">
			<portlet:resourceURL var='SubGridHeaderRow2' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractBudgetSeasonalEmployee.grid"/>
			</portlet:resourceURL>
			<portlet:resourceURL var='loadGridData2' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="seasonalEmployeeGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
				<portlet:param name="gridLabel" value="contractBudgetSeasonalEmployee.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
				<portlet:resourceURL var='seasonalEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="seasonalEmployeeGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL> 
			
			<c:set var="gridColNames2"><%=HHSUtil.getHeader("contractBudgetSeasonalEmployee.grid")%></c:set> 
			<c:set var="gridColProp2"><%=HHSUtil.getHeaderProp("contractBudgetSeasonalEmployee.grid")%></c:set> 
			<c:set var="subGridColProp2"><%=HHSUtil.getSubGridProp("contractBudgetSeasonalEmployee.grid")%></c:set> 
			<br>
			<jq:grid id="seasonalEmployeeGrid-${subBudgetId}" 
			        isReadOnly="true"
			         gridColNames="${gridColNames2}" 
				     gridColProp="${gridColProp2}" 
				     subGridColProp="{editable:true, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
			                  {editable:true, editrules:{isMandatoryField},editoptions:{dataInit: function (elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMax:'99999.99'});}, 100);}}},
			                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
			                  {editable:false, editrules:{required:true,number:true}},
			                  {editable:false, editrules:{required:true,number:true}}"
					 gridUrl="${SubGridHeaderRow2}"
					 subGridUrl="${loadGridData2}"
				     cellUrl="${seasonalEmployeeOperationGrid}"
				     editUrl="${seasonalEmployeeOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="budgetAmount,-invoicedAmount"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         nonEditColumnName="invoicedAmount,remainingAmount"    
			         positiveCurrency="budgetAmount" 
			   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
			   	     dropDownData="${personnelServiceMasterData}"
			   	     isExpandOnLoad="true"
			/>
			</c:if>
			<!-- R6 - Hiding the seasonal grid for new budget end -->
			
			<portlet:resourceURL var='SubGridHeaderRow3' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractBudgetFringeBenifits.grid"/>
			</portlet:resourceURL>
			<portlet:resourceURL var='loadGridData3' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="fringeBenifitsGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
				<portlet:param name="gridLabel" value="contractBudgetFringeBenifits.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<portlet:resourceURL var='fringeBenifitsOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="fringeBenifitsGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL> 
			<c:choose>
				<c:when test="${existingBudget eq 0}">
				<c:set var="gridColNames3"><%=HHSUtil.getHeader("contractBudgetFringeSummary.grid")%></c:set> 
				<c:set var="gridColProp3"><%=HHSUtil.getHeaderProp("contractBudgetFringeSummary.grid")%></c:set> 
				<c:set var="subGridColProp3"><%=HHSUtil.getSubGridProp("contractBudgetFringeSummary.grid")%></c:set>
				</c:when>
				<c:otherwise>
				<c:set var="gridColNames3"><%=HHSUtil.getHeader("contractBudgetFringeBenifits.grid")%></c:set> 
				<c:set var="gridColProp3"><%=HHSUtil.getHeaderProp("contractBudgetFringeBenifits.grid")%></c:set> 
				<c:set var="subGridColProp3"><%=HHSUtil.getSubGridProp("contractBudgetFringeBenifits.grid")%></c:set> 
				</c:otherwise>
			</c:choose>
			<br>
			<jq:grid id="fringeBenifitsGrid-${subBudgetId}_" 
			         isReadOnly="true"
			         gridColNames="${gridColNames3}" 
				     gridColProp="${gridColProp3}" 
				     subGridColProp="${subGridColProp3}" 
					 gridUrl="${loadGridData3}"
					 subGridUrl=""
				     cellUrl="${fringeBenifitsOperationGrid}"
				     editUrl="${fringeBenifitsOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="budgetAmount,-invoicedAmount"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="false"
			         positiveCurrency="budgetAmount" 
					 nonEditColumnName="invoicedAmount,remainingAmount"    
			   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
			   	     callbackFnAfterLoadGrid="populateRateValue('table_fringeBenifitsGrid-${subBudgetId}_>tbody>tr:eq(1)>td:eq(1)',${existingBudget}, ${subBudgetId});"
			   	     isExpandOnLoad="true"
			/>

			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
				<jsp:include page="programIncome.jsp">
					<jsp:param value="1" name="entryTypeId" />
				</jsp:include>
              &nbsp;
            </c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@2_')}">
		<div id ='2_${subBudgetId}' >
			<H3>OTPS - Operations and Support</H3>
			<div class='formcontainer paymentFormWrapper' style='width:100%'>
				<div class="row">
					<span class="label">Total Operations, Support and Equipment:</span>
					<span class="formfield">
						<span class='lftAmount'>
							<label  id="fyBudgetOTPS${subBudgetId}">${loCBOperationSupportBean.fyBudget}</label>
						</span>
					</span>
				</div>
				<div class="row">
				  	<span class="label">Total YTD Invoiced Amount:</span>
				  	<span class="formfield">
					  	<span class='lftAmount'>
					  		<label  id="ytdInvAmtOTPS${subBudgetId}">${loCBOperationSupportBean.ytdInvoicedAmt}</label>
					  	</span>
				  </span>
				</div>  
			 </div>
			 
			 <div class='clear'>&nbsp;</div>
			 
			 <%-- Below resource url is for Operation and Support grid headers --%>
			<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="operationAndSupport.grid"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("operationAndSupport.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("operationAndSupport.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("operationAndSupport.grid")%></c:set>
			
			<%-- Below resource url is for fetching/onload of operation and support grid data--%>
			<portlet:resourceURL var='loadOperationSupportGrid' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="operationAndSupportGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBOperationSupportBean"/>
				<portlet:param name="gridLabel" value="operationAndSupport.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<%-- Below resource url is for operations on operation and support grid --%>
			<portlet:resourceURL var='operationSupportGridActions' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="operationAndSupportGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBOperationSupportBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<jq:grid id="operationAndSuppGrid-${subBudgetId}" 
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${SubGridHeaderRow}"
					 subGridUrl="${loadOperationSupportGrid}"
				     cellUrl="${operationSupportGridActions}"
				     editUrl="${operationSupportGridActions}"
				     dataType="json" methodType="POST"
				     columnTotalName="fyBudget,-ytdInvoicedAmt"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         positiveCurrency="fyBudget"
			         isReadOnly="true"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     callbackFunction="refreshNonGridDataContBudOTPS('${subBudgetId}');"
				     isExpandOnLoad="true"
			/>
			<div>&nbsp;</div>
			
			<%-- Below resource url is for Equipment grid headers --%>
			<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="equipment.grid"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("equipment.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("equipment.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("equipment.grid")%></c:set>
			
			<%-- Below resource url is for fetching/onload of equipment grid data--%>
			<portlet:resourceURL var='loadEquipmentGrid' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="equipmentDetailsGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBEquipmentBean"/>
				<portlet:param name="gridLabel" value="equipment.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<%-- Below resource url is for operations on equipment grid --%>
			<portlet:resourceURL var='equipmentGridActions' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="equipmentDetailsGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBEquipmentBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			  
			<jq:grid id="equipmentGrid-${subBudgetId}" 
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${SubGridHeaderRow}"
					 subGridUrl="${loadEquipmentGrid}"
				     cellUrl="${equipmentGridActions}"
				     editUrl="${equipmentGridActions}"
				     dataType="json" methodType="POST"
				     columnTotalName="fyBudget,-ytdInvoicedAmt"
			         isPagination="false" 
			         nonEditColumnName="ytdInvoicedAmt,remainingAmt"
				     rowsPerPage="500"
			         isSubGrid="true"
			         positiveCurrency="fyBudget"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     callbackFunction="refreshNonGridDataContBudOTPS('${subBudgetId}');"
				     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
				<jsp:include page="programIncome.jsp">
					<jsp:param value="2" name="entryTypeId" />
				</jsp:include>
              &nbsp;
            </c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@3_')}">
		<div id ='3_${subBudgetId}' >
			<%-- 
			This jsp is used as a poc for grids with static headers.
			It will serve as a reference while creating page specs jsps having grids.
			 --%>
			<c:set var="readOnlyStatusIndirectRate">
				<fmt:message key='BUDGET_PENDING_APPROVAL'/>,
				<fmt:message key="BUDGET_APPROVED"/>,
				<fmt:message key="BUDGET_ACTIVE"/>,
				<fmt:message key="BUDGET_CANCELLED"/>,
				<fmt:message key="BUDGET_SUSPENDED"/>,
				<fmt:message key="BUDGET_CLOSED"/>
			</c:set>
			<H3>OTPS - Utilities</H3>
		
			<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractBudgetUtility.grid"/>
			</portlet:resourceURL>
			
			<portlet:resourceURL var='loadBudgetRate' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="utilitiesGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
				<portlet:param name="gridLabel" value="contractBudgetUtility.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<portlet:resourceURL var='utilityOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="utilitiesGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBUtilities"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetUtility.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetUtility.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetUtility.grid")%></c:set>
			
			<jq:grid id="utilitiesGrid-${subBudgetId}" 
			        isReadOnly="true"
				    gridColNames="${gridColNames}" 
				    gridColProp="${gridColProp}" 
				    subGridColProp="${subGridColProp}" 
					gridUrl="${RateSubGridHeaderRow}"
					subGridUrl="${loadBudgetRate}"
				    cellUrl="${utilityOperationGrid}"
				    editUrl="${utilityOperationGrid}"
				    dataType="json" methodType="POST"
				    columnTotalName="fyAmount,-invoiceAmount"
				    isPagination="false"
				    rowsPerPage="0"
				    isSubGrid="true"
				    nonEditColumnName="invoiceAmount,remainingAmt"
				    positiveCurrency="fyAmount"
				    operations="del:false,edit:false,add:false,cancel:false,save:false"
				    isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
				<jsp:include page="programIncome.jsp">
					<jsp:param value="3" name="entryTypeId" />
				</jsp:include>
              &nbsp;
            </c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@4_')}">
		<div id ='4_${subBudgetId}'>
			<%-- 
			This jsp is used as a poc for grids with static headers.
			It will serve as a reference while creating page specs jsps having grids.
			 --%>
			 
			 <H3>OTPS - Professional Services</H3>
			 
			<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="professionalServices.grid"/>
			</portlet:resourceURL>
	
			<c:set var="gridColNames"><%=HHSUtil.getHeader("professionalServices.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("professionalServices.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("professionalServices.grid")%></c:set>
			<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="profServicesGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBProfessionalServicesBean"/>
				<portlet:param name="gridLabel" value="professionalServices.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<portlet:resourceURL var='professionalServicesGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="profServicesGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBProfessionalServicesBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<c:set var="readOnlyStatusProfService">
				<fmt:message key='BUDGET_PENDING_APPROVAL'/>,
				<fmt:message key="BUDGET_APPROVED"/>,
				<fmt:message key="BUDGET_ACTIVE"/>,
				<fmt:message key="BUDGET_CANCELLED"/>,
				<fmt:message key="BUDGET_SUSPENDED"/>,
				<fmt:message key="BUDGET_CLOSED"/>
			</c:set>
			
			<jq:grid id="profServicesGrid-${subBudgetId}" 
			        isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${SubGridHeaderRow}"
					 subGridUrl="${loadGridData}"
				     cellUrl="${professionalServicesGrid}"
				     editUrl="${professionalServicesGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="fyBudget,-ytdInvoicedAmt"
				     positiveCurrency="fyBudget"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         lastRowEdit="${isLastRowEdit}"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
				<jsp:include page="programIncome.jsp">
					<jsp:param value="4" name="entryTypeId" />
				</jsp:include>
              &nbsp;
            </c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@5_')}">
		<div id ='5_${subBudgetId}'>
			<%-- 
			This jsp is used for S319 Rent screen.
			--%>
			<c:set var="readOnlyStatusRent">
				<fmt:message key='BUDGET_PENDING_APPROVAL'/>,
				<fmt:message key="BUDGET_APPROVED"/>,
				<fmt:message key="BUDGET_ACTIVE"/>,
				<fmt:message key="BUDGET_CANCELLED"/>,
				<fmt:message key="BUDGET_SUSPENDED"/>,
				<fmt:message key="BUDGET_CLOSED"/>
			</c:set>
			
			<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractBudgetRent.grid"/>
			</portlet:resourceURL>
			
			<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="contractBudgetRent"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
				<portlet:param name="gridLabel" value="contractBudgetRent.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<portlet:resourceURL var='RentOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="contractBudgetRent"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetRent.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetRent.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetRent.grid")%></c:set>
			
			<h3>OTPS - Rent</h3>
			
			<jq:grid id="rentGrid-${subBudgetId}" 
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="{editable:true, editrules:{isMandatoryField},editoptions:{maxlength:50}},
			                 {editable:true, editrules:{isMandatoryField},editoptions:{maxlength:50}},
			                 {editable:true, editrules:{isMandatoryField},editoptions:{maxlength:50}},
			                 {editable:true, editrules:{required:true},edittype : 'select',editoptions : {value : '1:Yes;0:No'}},
			                 {editable:true, editrules:{required:true,number:true,allowOnlyPercentValue},editoptions:{dataInit:function(elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMax:'100.00'});},100);}}},
			                 {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
			                 {editable:false},
			                 {editable:false}"
					 gridUrl="${SubGridHeaderRow}"
					 subGridUrl="${loadGridData}"
				     cellUrl="${RentOperationGrid}"
				     editUrl="${RentOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="fyBudget,-ytdInvoiceAmt"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true" autoWidth="true"
			   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	     positiveCurrency="fyBudget" 
			   	     nonEditColumnName="ytdInvoiceAmt,remainingAmt"
			   	     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
				<jsp:include page="programIncome.jsp">
					<jsp:param value="5" name="entryTypeId" />
				</jsp:include>
              &nbsp;
            </c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>		
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@6_')}">
		<div id ='6_${subBudgetId}'>
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
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${contractServicesSubGridHeaderRow}"
					 subGridUrl="${loadBudgetContractedConsultants}"
				     cellUrl="${contractedServicesConsultantsOperationGrid}"
				     editUrl="${contractedServicesConsultantsOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="fyBudget,-ytdInvoiceAmt"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         nonEditColumnName="ytdInvoiceAmt,remaingAmt"
			         positiveCurrency="fyBudget"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"
				     isExpandOnLoad="true"
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
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${contractedServicesSubContractorsSubGridHeaderRow}"
					 subGridUrl="${loadBudgetContractedSubContractors}"
				     cellUrl="${contractedServicesSubContractorsOperationGrid}"
				     editUrl="${contractedServicesSubContractorsOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="fyBudget,-ytdInvoiceAmt"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         nonEditColumnName="ytdInvoiceAmt,remaingAmt"
			         positiveCurrency="fyBudget"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"
				     isExpandOnLoad="true"
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
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${contractedServicesVendorsSubGridHeaderRow}"
					 subGridUrl="${loadBudgetContractedVendors}"
				     cellUrl="${contractedServicesVendorsOperationGrid}"
				     editUrl="${contractedServicesVendorsOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="fyBudget,-ytdInvoiceAmt"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         nonEditColumnName="ytdInvoiceAmt,remaingAmt"
			         positiveCurrency="fyBudget"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"
				     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
				<jsp:include page="programIncome.jsp">
					<jsp:param value="6" name="entryTypeId" />
				</jsp:include>
              &nbsp;
            </c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@7_')}">		
		<div id ='7_${subBudgetId}'>		
		
			<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractBudgetRate.grid"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetRate.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetRate.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetRate.grid")%></c:set>
			<portlet:resourceURL var='loadBudgetRate' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="getContractBudgetRateGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
				<portlet:param name="gridLabel" value="contractBudgetRate.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<portlet:resourceURL var='rateOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="getContractBudgetRateGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.RateBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<H3>Rate</H3>
			<jq:grid id="budgetRateGrid-${subBudgetId}" 
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${RateSubGridHeaderRow}"
					 subGridUrl="${loadBudgetRate}"
				     cellUrl="${rateOperationGrid}"
				     editUrl="${rateOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="fyBudget,-ytdInvoiceAmt"
			         isPagination="false"
				     rowsPerPage="22"
			         isSubGrid="true"
			         nonEditColumnName="ytdUnits,remUnits,ytdInvoiceAmt,remainAmt"
			         positiveCurrency="fyBudget" 
			         operations="del:false,edit:false,add:false,cancel:false,save:false"
			         isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
				<jsp:include page="programIncome.jsp">
					<jsp:param value="7" name="entryTypeId" />
				</jsp:include>
              &nbsp;
            </c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@8_')}">
		<div id ='8_${subBudgetId}'>
			<H3>Milestone</H3>
	
			<portlet:resourceURL var='MilestoneSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractBudgetMilestone.grid"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetMilestone.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetMilestone.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetMilestone.grid")%></c:set>
			
			<%--This portlet:resourceURL is for Grid data load call --%>
			<portlet:resourceURL var='loadBudgetMilestone' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="getContractBudgetMilestoneGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBMileStoneBean"/>
				<portlet:param name="gridLabel" value="contractBudgetMilestone.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<%--This portlet:resourceURL is for Grid data operations call --%>
			<portlet:resourceURL var='milestoneOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="getContractBudgetMilestoneGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBMileStoneBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			
			<jq:grid id="milestoneGrid-${subBudgetId}" 
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${MilestoneSubGridHeaderRow}"
					 subGridUrl="${loadBudgetMilestone}"
				     cellUrl="${milestoneOperationGrid}"
				     editUrl="${milestoneOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="amount,-invoiceAmount"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         nonEditColumnName="invoiceAmount,remainAmt"
			         positiveCurrency="amount"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${oldPIFlag eq 0}">
				<jsp:include page="programIncome.jsp">
					<jsp:param value="8" name="entryTypeId" />
				</jsp:include>
              &nbsp;
            </c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@9_')}">
		<div id ='9_${subBudgetId}'>
			<H3>Unallocated Funds</H3>
	
			<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow'escapeXml='false'>
				<portlet:param name="gridLabel" value="amendment.UnallocatedFunds.grid" />
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("amendment.UnallocatedFunds.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("amendment.UnallocatedFunds.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("amendment.UnallocatedFunds.grid")%></c:set>
			
			<%-- loading the page  --%>
			<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="unallocatedFunds" />
				<portlet:param name="beanName" value="com.nyc.hhs.model.UnallocatedFunds" />
				<portlet:param name="gridLabel" value="amendment.UnallocatedFunds.grid" />
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<%-- operations for Edit/Update/Save --%>
			<portlet:resourceURL var='UnallocatedFundsOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="unallocatedFunds" />
				<portlet:param name="beanName" value="com.nyc.hhs.model.UnallocatedFunds" />
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<%-- JGrid for adding dynamic table --%>
			<jq:grid id="unallocatedFundsGrid-${subBudgetId}" 
			 isReadOnly="true"
				gridColNames="${gridColNames}"
				gridColProp="${gridColProp}" 
				subGridColProp="${subGridColProp}"
				gridUrl="${SubGridHeaderRow}"
				subGridUrl="${loadGridData}"
				cellUrl="${UnallocatedFundsOperationGrid}"
				editUrl="${UnallocatedFundsOperationGrid}"
				dataType="json"
				methodType="POST" 
				columnTotalName="" 
				isPagination="false"
				rowsPerPage="500" isSubGrid="true"
				positiveCurrency="ammount"  
				operations="del:false,edit:false,add:false,cancel:false,save:false"
				isExpandOnLoad="true" 
			/>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@10_')}">
		<div id ='10_${subBudgetId}'>
			<c:set var="readOnlyStatusIndirectRate">
				<fmt:message key='BUDGET_PENDING_APPROVAL'/>,
				<fmt:message key="BUDGET_APPROVED"/>,
				<fmt:message key="BUDGET_ACTIVE"/>,
				<fmt:message key="BUDGET_CANCELLED"/>,
				<fmt:message key="BUDGET_SUSPENDED"/>,
				<fmt:message key="BUDGET_CLOSED"/>
			</c:set>
			
			<%-- 
			This resource url used to refresh non grid data.
			--%>
			<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractBudgetIndirectRate.grid"/>
			</portlet:resourceURL>
			<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="contractBudgetIndirectRate"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBIndirectRateBean"/>
				<portlet:param name="gridLabel" value="contractBudgetIndirectRate.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<portlet:resourceURL var='indirectRateOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="contractBudgetIndirectRate"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBIndirectRateBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
	
			<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetIndirectRate.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetIndirectRate.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetIndirectRate.grid")%></c:set>
			
			<h3>Indirect Rate </h3>
			
			<div class="formcontainer paymentFormWrapper widthFull">
				<div class="row">
				      <span class="label">Indirect Rate - City Funded:</span>
				      <span class="formfield">
				      	<span class='indirectRate${subBudgetId}' id="indirectRate${subBudgetId}">${indirectPercentage}</span><span>%</span>
				      </span>
				</div>
			</div>
			
			<div class='clear'>&nbsp;</div>
				
			<jq:grid id="indirectRateGrid-${subBudgetId}" 
			        isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${SubGridHeaderRow}"
					 subGridUrl="${loadGridData}"
				     cellUrl="${indirectRateOperationGrid}"
				     editUrl="${indirectRateOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName="indirectAmount,-ytdInvoiceAmount"
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	     callbackFunction="refreshNonGridIndirectRateData('${subBudgetId}');"
			   	     positiveCurrency="indirectAmount"
			   	     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
				<jsp:include page="programIncome.jsp">
					<jsp:param value="10" name="entryTypeId" />
				</jsp:include>
              &nbsp;
            </c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>

	<c:if test="${fn:contains(tabToShowList, '@###@11_')}">
	<%--  Start: Added in R7: Program Income --%>
	<c:if test="${oldPIFlag ne 0}">   
		<div id ='11_${subBudgetId}'>
			<H3>Program Income</H3>
	
			<%--getting Sub-Grid header  --%>
			<portlet:resourceURL var='programIncomeSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="programIncome.grid"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("programIncome.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("programIncome.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("programIncome.grid")%></c:set>
			
			<%-- loading the page  --%>
			<portlet:resourceURL var='loadBudgetProgramIncome' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="programIncomeGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBProgramIncomeBean"/>
				<portlet:param name="gridLabel" value="programIncome.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<%-- operations for Edit/Update/Save --%>
			<portlet:resourceURL var='programIncomeOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="programIncomeGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBProgramIncomeBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<%-- JQ Grid Starts--%>
			<jq:grid id="programIncomeGrid-${subBudgetId}" 
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${programIncomeSubGridHeaderRow}"
					 subGridUrl="${loadBudgetProgramIncome}"
				     cellUrl="${programIncomeOperationGrid}"
				     editUrl="${programIncomeOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName=""
			         isPagination="false"
				     rowsPerPage="7"
			         isSubGrid="true"         
			         positiveCurrency="fYBudget"        
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     isExpandOnLoad="true"
			/>
			<%-- JQ Grid Ends--%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
		</c:if>
		<c:if test="${oldPIFlag eq 0}">   
		<div id ='11_${subBudgetId}'>
			<H3>Program Income</H3>

				<%--getting Sub-Grid header  --%>
				<portlet:resourceURL var='newProgramIncomeSubGridHeaderRow'
					id='SubGridHeaderRow' escapeXml='false'>
					<portlet:param name="gridLabel" value="programIncomeNew.grid" />
				</portlet:resourceURL>

				<c:set var="gridColNames"><%=HHSUtil.getHeader("programIncomeNew.grid")%></c:set>
				<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("programIncomeNew.grid")%></c:set>
				<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("programIncomeNew.grid")%></c:set>

				<%-- loading the page  --%>
				<portlet:resourceURL var='loadBudgetProgramIncome' id='loadGridData'
					escapeXml='false'>
					<portlet:param name="transactionName" value="programIncomeGrid" />
					<portlet:param name="beanName"
						value="com.nyc.hhs.model.CBProgramIncomeBean" />
					<portlet:param name="gridLabel" value="programIncomeNew.grid" />
					<portlet:param name="subBudgetId" value="${subBudgetId}" />
				</portlet:resourceURL>

				<%-- operations for Edit/Update/Save --%>
				<portlet:resourceURL var='programIncomeOperationGrid'
					id='gridOperation' escapeXml='false'>
					<portlet:param name="transactionName" value="programIncomeGrid" />
					<portlet:param name="beanName"
						value="com.nyc.hhs.model.CBProgramIncomeBean" />
					<portlet:param name="subBudgetId" value="${subBudgetId}" />
				</portlet:resourceURL>

				<%-- JQ Grid Starts--%>
				<jq:grid id="newProgramIncomeGrid-${subBudgetId}"
					isReadOnly="true"
					gridColNames="${gridColNames}" 
					gridColProp="${gridColProp}"
					subGridColProp="${subGridColProp}"
					gridUrl="${newProgramIncomeSubGridHeaderRow}"
					subGridUrl="${loadBudgetProgramIncome}"
					cellUrl="${programIncomeOperationGrid}"
					editUrl="${programIncomeOperationGrid}" 
					dataType="json"
					methodType="POST" columnTotalName="" 
					isPagination="true"
					rowsPerPage="10" 
					isSubGrid="true" 
					positiveCurrency="fYBudget"
					operations="del:true,edit:true,add:true,cancel:true,save:true"
					dropDownData="${programIncomeData}"
					 callbackFunction="refreshNonGridIndirectRateData('${subBudgetId}');"
					 />
				<%-- JQ Grid Ends--%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
		</c:if>
		<%--  End R7: Program Income --%>
	</c:if>
<%-- start: Added in R7 for cost center --%>
	
<c:if test="${costCenterOpted eq 2}">
	
<div id ='12_${subBudgetId}'>
<H3>Services</H3>
	
<%--getting Sub-Grid header  --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetServices.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetServices.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetServices.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetServices.grid")%></c:set>

<%-- Below resource url is for fetching/onload of equipment grid data--%>
<portlet:resourceURL var='loadServicesGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="servicesGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="gridLabel" value="contractBudgetServices.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Below resource url is for operations on equipment grid --%>
<portlet:resourceURL var='servicesGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="servicesGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>



<jq:grid id="serviceGrid-${subBudgetId}" 
         isReadOnly="true"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadServicesGrid}"
	     cellUrl="${servicesGridActions}"
	     editUrl="${servicesGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,-ytdInvoicedAmt"
         isPagination="true" 
         nonEditColumnName="ytdInvoicedAmt,remainingAmt"
	     rowsPerPage="10"
         isSubGrid="true"
         positiveCurrency="fyBudget"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
/>
<%-- JQ Grid Ends--%>

<div>&nbsp;</div><hr><div>&nbsp;</div>
</div>
<H3>Cost Center</H3>
	
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetCostCenter.grid"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetCostCenter.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetCostCenter.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetCostCenter.grid")%></c:set>

<portlet:resourceURL var='loadCostCenterGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="costCenterGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="gridLabel" value="contractBudgetCostCenter.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<portlet:resourceURL var='costCenterGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="costCenterGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>


<jq:grid id="costCenterGrid-${subBudgetId}" 
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadCostCenterGrid}"
	     cellUrl="${costCenterGridActions}"
	     editUrl="${costCenterGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,-ytdInvoicedAmt"
         isPagination="false" lastRowEdit="${lastRowEditable}"
	     rowsPerPage="10"
         isSubGrid="true"
         positiveCurrency="fyBudget"
         isReadOnly="${readOnlyPageAttribute}"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
	     callbackFunction="refreshNonGridDataContBudOTPS('${subBudgetId}');"
/> 
	
</c:if>
<%-- End: Added in R7 for cost center --%>
</div>