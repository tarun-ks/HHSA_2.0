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

<script type="text/javascript">
$(document).ready(function() {
	$(".invoiceCurrencyCheck").each(function(e) {
		$(this).validateCurrencyOnLoad();
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

//function to expand and collapse headers
function showme(id, linkid) {
	var divid = document.getElementById(id);
	var toggleLink = document.getElementById(linkid);
	if (divid.style.display == '') {
		toggleLink.innerHTML = '+';
		divid.style.display = 'none';
	} else {
		toggleLink.innerHTML = '-';
		divid.style.display = '';
	}
}

/**
* This function called On loadcomplete to copy fringe benefit rate on jqgrid(fringe benefit) header
* Added in Release 6 for Defect 8498
**/
function populateRateValue(tableId, existingBudget, subBudgetId){
	if(existingBudget == '0')
 	{
		var objTbId = $('#' + tableId);
		var tbIdVal = $('#val5'+ subBudgetId).html().replace('(','').replace(')','');
		objTbId.html(tbIdVal);
		objTbId.attr('title', tbIdVal);
	}
}
</script>
<style type="text/css">
	objHidden {display:none}
</style>

<div style='margin: 20px' >

	<c:set var="tabToShowList" value="${tabToShowList}"/>
	
	<div class="budgetSummary">
		<c:set var="changeId" value="${subBudgetId}"></c:set>
		<h3>Budget Summary</h3> 
		<table width="100%" cellspacing='0' cellpadding='0'>				
				<tr>
					<th colspan='2' class='alignCenter'>Line Item</th>
					<th class='alignCenter'>FY Budget</th>
					<th class='alignCenter'>YTD Invoiced Amount</th>
					<th class='alignCenter'>Remaining Amount</th>
					<th class='alignCenter'>Invoice Amount</th>
				</tr>
				<tr>
					<td width='3%' class='togglePlaceholder' id="togglerMain${changeId}" onclick="showme('taggingMain${changeId}', this.id);"><span>-</span></td>
					<td width='45%' class='bold'>${CBInvoiceSummary.totalCityFundedBudget.title}</td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalCityFundedBudget.approvedBudget}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalCityFundedBudget.ytdInvoicedAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalCityFundedBudget.remainingAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalCityFundedBudget.invoicedAmount}"/></label></td>
				</tr>
		</table>
		<div id='taggingMain${changeId}'>
			<%-- "Total Direct Costs" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0'>				
				<tr>
					<td width='40%' class='noBdr'><h3>${CBInvoiceSummary.totalDirectsCosts.title}</h3></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalDirectsCosts.approvedBudget}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalDirectsCosts.ytdInvoicedAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalDirectsCosts.remainingAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalDirectsCosts.invoicedAmount}"/></label></td>
				</tr>
			</table>
			<%-- "Total Direct Costs" table Ends Here --%>		
				
				
			<%-- "Total Salary and Fringe" Table Starts --%>
			<table width="92%" cellspacing='0' cellpadding='0'>					
				<tr>
					<td width='3%' class='togglePlaceholder' id="toggler${changeId}" onclick="showme('tagging${changeId}', this.id);"><span>-</span></td>
					<td width='37%' class='bold'>${CBInvoiceSummary.totalSalaryAndFringesAmount.title}</td>
							<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalaryAndFringesAmount.approvedBudget}"/></label></td>
							<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalaryAndFringesAmount.ytdInvoicedAmount}"/></label></td>
							<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalaryAndFringesAmount.remainingAmount}"/></label></td>
							<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalaryAndFringesAmount.invoicedAmount}"/></label></td>		
				</tr>
					<tbody id="tagging${changeId}">
					<tr >
						<td>&nbsp;</td>
							<td>${CBInvoiceSummary.totalSalary.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalary.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalary.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalary.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalary.invoicedAmount}"/></label></td>		
					</tr>
					<tr>
						<td>&nbsp;</td>
							<td><label>${CBInvoiceSummary.totalFringes.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalFringes.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalFringes.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalFringes.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalFringes.invoicedAmount}"/></label></td>		
					</tr>
				</tbody>
			</table>
			<%-- "Total Salary and Fringe" Table Ends Here --%>
				
				
			<%-- "Total OTPS" Table starts --%>
			<table width="92%" cellspacing='0' cellpadding='0' class='objHidden'>				
					<tr>
						<td width='3%' class='togglePlaceholder' id="toggler2${changeId}" onclick="showme('tagging2${changeId}', this.id);"><span>-</span></td>
						<td width='37%' class='bold'>${CBInvoiceSummary.totalOTPSAmount.title}</td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalOTPSAmount.approvedBudget}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalOTPSAmount.ytdInvoicedAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalOTPSAmount.remainingAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalOTPSAmount.invoicedAmount}"/></label></td>
					</tr>
					<tbody id='tagging2${changeId}'>
						<tr>
							<td>&nbsp;</td>
							<td>${CBInvoiceSummary.operationsAndSupportAmount.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.operationsAndSupportAmount.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.operationsAndSupportAmount.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.operationsAndSupportAmount.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.operationsAndSupportAmount.invoicedAmount}"/></label></td>		
						</tr>
					
					
						<tr>
							<td>&nbsp;</td>
							<td>${CBInvoiceSummary.utilitiesAmount.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.utilitiesAmount.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.utilitiesAmount.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.utilitiesAmount.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.utilitiesAmount.invoicedAmount}"/></label></td>		
						</tr>
					
					
						<tr>
							<td>&nbsp;</td>
							<td>${CBInvoiceSummary.professionalServicesAmount.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.professionalServicesAmount.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.professionalServicesAmount.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.professionalServicesAmount.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.professionalServicesAmount.invoicedAmount}"/></label></td>		
						</tr>
					
					
						<tr>
							<td>&nbsp;</td>
							<td>${CBInvoiceSummary.rentAndOccupancyAmount.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.rentAndOccupancyAmount.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.rentAndOccupancyAmount.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.rentAndOccupancyAmount.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.rentAndOccupancyAmount.invoicedAmount}"/></label></td>		
						</tr>
						
						<tr>
							<td>&nbsp;</td>
							<td>${CBInvoiceSummary.contractedServicesAmount.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.contractedServicesAmount.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.contractedServicesAmount.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.contractedServicesAmount.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.contractedServicesAmount.invoicedAmount}"/></label></td>	
						</tr>
					
					</tbody>
			</table>
			<%-- "Total OTPS" Table Ends here --%>
				
				
			<%-- "Total Rate Based" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0' class='summaryWrapper2'>				
				<tr>
						<td width='40%' class='bold'>${CBInvoiceSummary.totalRateBasedAmount.title}</td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalRateBasedAmount.approvedBudget}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalRateBasedAmount.ytdInvoicedAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalRateBasedAmount.remainingAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalRateBasedAmount.invoicedAmount}"/></label></td>
				</tr>
			</table>
			<%-- "Total Rate Based" table Ends Here --%>	
			
			
			<%-- "Total Milestone Based" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0' class='summaryWrapper2'>				
				<tr>
						<td width='40%' class='bold'>${CBInvoiceSummary.totalMilestoneBasedAmount.title}</label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalMilestoneBasedAmount.approvedBudget}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalMilestoneBasedAmount.ytdInvoicedAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalMilestoneBasedAmount.remainingAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalMilestoneBasedAmount.invoicedAmount}"/></label></td>
				</tr>
			</table>
			<%-- "Total Milestone Based" table Ends Here --%>	
			
			
			<%-- "Unallocated Funds" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0' class=''>				
				<tr>
						<td width='40%' class='bold'>${CBInvoiceSummary.unallocatedFunds.title}</td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.unallocatedFunds.approvedBudget}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.unallocatedFunds.ytdInvoicedAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.unallocatedFunds.remainingAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.unallocatedFunds.invoicedAmount}"/></label></td>
				</tr>
			</table>
			<%-- "Unallocated Funds" table Ends Here --%>
			
			<h3 style='margin-left:8%;'>Total Indirect Costs</h3>
			<p style='margin-left:7%;'>
				&nbsp;&nbsp;<b>Indirect Rate - City Funded</b> &nbsp;
			${asIndirectRate}  %
			</p>
			
			<%-- "Total Indirect Costs" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0'>				
				<tr>
						<td width='40%' class='bold'>${CBInvoiceSummary.totalIndirectCosts.title}</td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalIndirectCosts.approvedBudget}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalIndirectCosts.ytdInvoicedAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalIndirectCosts.remainingAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalIndirectCosts.invoicedAmount}"/></label></td>
				</tr>
			</table>
			<%-- "Total Indirect Costs" table Ends Here --%>	
		</div>
		<hr>
		<table width="100%" cellspacing='0' cellpadding='0'>				
			<tr>
				<td width='3%'>&nbsp;</td>
					<td width='45%' class='bold'>${CBInvoiceSummary.totalProgramIncome.title}
					<div>(Excluded from City Funded Budget; Not Invoiced)</div>
				</td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramIncome.approvedBudget}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramIncome.ytdInvoicedAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramIncome.remainingAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramIncome.invoicedAmount}"/></label></td>
			</tr>
	    </table>
		<table width="100%" cellspacing='0' cellpadding='0'>				
			<tr>
			
				<td width='48%' class='bold'>${CBInvoiceSummary.totalProgramBudget.title}  
					<div>(City Funded Budget + Program Income)</div>
				</td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramBudget.approvedBudget}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramBudget.ytdInvoicedAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramBudget.remainingAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramBudget.invoicedAmount}"/></label></td>
			</tr>
	    </table>
	    <div>&nbsp;</div><hr><div>&nbsp;</div>			
	</div>
	<c:if test="${fn:contains(tabToShowList, '@###@1_')}">
		<div id ='1_${subBudgetId}'>
			<h3>Personnel Services</h3>
			<div class="formcontainer paymentFormWrapper widthFull">
			<c:choose>
			<%-- Start: Added for 8498 --%>
				<c:when test="${usesFte eq 0}">
				 <div class="row">
      				<span class="label">City Salary &amp; Fringe:</span>
     				<span class="formfield">
      					<span class='lftAmount'><label id="val1${subBudgetId}">${personnelServiceData.totalSalaryAndFringeAmount}</label></span>
     				</span>
 				</div>
  				<div class="row">
      				<span class="label">City Salary:</span>
      				<span class="formfield">
      					<span class='lftAmount'><label id="val2${subBudgetId}" >${personnelServiceData.totalSalaryAmount}</label></span>
      				</span>
    			</div>  
				<div class="row">
  					<span class="label">City Fringe:</span>
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
				<div class="row">
				  <span class="label">Total Positions:</span>
				  <span class="formfield">
				  	<span class='lftAmount'><label id="val4${subBudgetId}">${personnelServiceData.totalPositions}</label></span>
				  </span>  
				</div>
				</c:when>
				<%-- End: Added for 8498 --%>
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
				</c:otherwise>
			</c:choose>
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
			<c:choose>
				<c:when test="${usesFte eq 0}">
				<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceSalariedEmployeeSummary.grid")%></c:set>
				<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceSalariedEmployeeSummary.grid")%></c:set>
				<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceSalariedEmployeeSummary.grid")%></c:set>
				</c:when>
				<c:otherwise>
				<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceSalariedEmployee.grid")%></c:set>
				<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceSalariedEmployee.grid")%></c:set>
				<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceSalariedEmployee.grid")%></c:set>
				</c:otherwise>
			</c:choose>
			
			<jq:grid id="salariedEmployeeGridInvoiceGrid-${subBudgetId}" 
			        isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}"
					 gridUrl="${SubGridHeaderRow}"
					 subGridUrl="${loadGridData}"
				     cellUrl="${salariedEmployeeOperationGrid}"
				     editUrl="${salariedEmployeeOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName=""
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         positiveCurrency="invoicedAmount" 
			         checkForTotalValue="invoicedAmount,remainingAmount,invoiceAmountEnteredCheck" 
			   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
			   	     isExpandOnLoad="true"   	     
			/>
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
			
			<c:choose>
				<c:when test="${usesFte eq 0}">
				<c:set var="gridColNames1"><%=HHSUtil.getHeader("invoiceHourlyEmployeeSummary.grid")%></c:set>
				<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("invoiceHourlyEmployeeSummary.grid")%></c:set>
				<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("invoiceHourlyEmployeeSummary.grid")%></c:set>
				</c:when>
				<c:otherwise>
				<c:set var="gridColNames1"><%=HHSUtil.getHeader("invoiceHourlyEmployee.grid")%></c:set> 
				<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("invoiceHourlyEmployee.grid")%></c:set> 
				<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("invoiceHourlyEmployee.grid")%></c:set>
				</c:otherwise>
			</c:choose> 
			<br>
			<jq:grid id="hourlyEmployeeGridInvoiceGrid-${subBudgetId}" 
			        isReadOnly="true"
			         gridColNames="${gridColNames1}" 
				     gridColProp="${gridColProp1}" 
				     subGridColProp="${subGridColProp1}"
					 gridUrl="${SubGridHeaderRow1}"
					 subGridUrl="${loadGridData1}"
				     cellUrl="${hourlyEmployeeOperationGrid}"
				     editUrl="${hourlyEmployeeOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName=""
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         positiveCurrency="invoicedAmount"          
			         checkForTotalValue="invoicedAmount,remainingAmount,invoiceAmountEnteredCheck" 
			   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
			   	     isExpandOnLoad="true"   	     
			/>
			<!-- R6 - Hiding the seasonal grid for new budget start -->
			<c:if test="${usesFte eq 1}">
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
			        isReadOnly="true"
			         gridColNames="${gridColNames2}" 
				     gridColProp="${gridColProp2}" 
				     subGridColProp="${subGridColProp2}"
					 gridUrl="${SubGridHeaderRow2}"
					 subGridUrl="${loadGridData2}"
				     cellUrl="${seasonalEmployeeOperationGrid}"
				     editUrl="${seasonalEmployeeOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName=""
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         positiveCurrency="invoicedAmount"          
			         checkForTotalValue="invoicedAmount,remainingAmount,invoiceAmountEnteredCheck" 
			   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
			   	     isExpandOnLoad="true"   	     
			/>
			</c:if>
			<!-- R6 - Hiding the seasonal grid for new budget end -->
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
			<c:choose>
				<c:when test="${usesFte eq 0}">
				<c:set var="gridColNames3"><%=HHSUtil.getHeader("invoiceFringeBenefitsSummary.grid")%></c:set>
				<c:set var="gridColProp3"><%=HHSUtil.getHeaderProp("invoiceFringeBenefitsSummary.grid")%></c:set>
				<c:set var="subGridColProp3"><%=HHSUtil.getSubGridProp("invoiceFringeBenefitsSummary.grid")%></c:set>
				</c:when>
				<c:otherwise>
				<c:set var="gridColNames3"><%=HHSUtil.getHeader("invoiceFringeBenefits.grid")%></c:set> 
				<c:set var="gridColProp3"><%=HHSUtil.getHeaderProp("invoiceFringeBenefits.grid")%></c:set> 
				<c:set var="subGridColProp3"><%=HHSUtil.getSubGridProp("invoiceFringeBenefits.grid")%></c:set> 
				</c:otherwise>
			</c:choose>
			<br>
			<jq:grid id="fringeBenefitsGridInvoiceGrid-${subBudgetId}_" 
			        isReadOnly="false"
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
			         positiveCurrency="invoicedAmount"          
			         checkForTotalValue="Invoice Amount,Remaining Amount,invoiceAmountEnteredCheck"
			   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
			   	     callbackFnAfterLoadGrid="populateRateValue('table_fringeBenefitsGridInvoiceGrid-${subBudgetId}_>tbody>tr:eq(1)>td:eq(1)',${usesFte}, ${subBudgetId});"
			   	     isExpandOnLoad="true"   	     
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
            &nbsp;
            <jsp:include page="programIncomeInvoice.jsp">
			  <jsp:param value="1" name="entryTypeId" />
			</jsp:include>
			</c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@2_')}">
		<div id ='2_${subBudgetId}' >
			<h3>OTPS - Operations and Support</h3>
			
			<div class="formcontainer paymentFormWrapper" style='width:100%'>
				<div class="row">
					  <span class="label">Invoice Total Operations, Support and Equipment : </span>
					  <span class="formfield">
					  	<span class='lftAmount'>
					  		<label id="fyBudgetOTPS${subBudgetId}">${invoiceTotalAmountsOTPS}</label>
					  	</span>
					  </span>
				</div>
				<div class="row">
					<span class="label">Total YTD Invoiced Amount : </span>
					<span class="formfield"> 
						<span class='lftAmount'>
							<label id="ytdInvAmtOTPS${subBudgetId}">${ytdInvoicedAmountOTPS}</label>
						</span>
					</span>
				</div>
			</div>
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
			         isReadOnly="true"
			         positiveCurrency="invoicedAmt"
				     operations="del:false,edit:false,add:false,cancel:false,save:false" 
				     callbackFunction="refreshNonGridDataInvoiceOTPS('${subBudgetId}');"
				     isExpandOnLoad="true"
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
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true" 
			         checkForTotalValue="invoicedAmt,remainingAmt,invoiceAmountMoreThanRemaining"
			         positiveCurrency="invoicedAmt"
			         isReadOnly="true"
				     operations="del:false,edit:false,add:false,cancel:false,save:false" 
				     callbackFunction="refreshNonGridDataInvoiceOTPS('${subBudgetId}');"
				     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
            &nbsp;
            <jsp:include page="programIncomeInvoice.jsp">
					<jsp:param value="2" name="entryTypeId" />
				</jsp:include>
			</c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@3_')}">
		<div id ='3_${subBudgetId}' >
			<H3>OTPS - Utilities</H3>
	
			<%-- This portlet resource maps the Action in Base Controller to display header data in Utilities Invoice grid  --%>
			<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="invoicingUtility.grid"/>
			</portlet:resourceURL>
			
			<%-- This portlet resource maps the Action in Base Controller to load data in Utilities Invoice grid  --%>
			<portlet:resourceURL var='loadBudgetRate' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="utilitiesInvoicingGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
				<portlet:param name="gridLabel" value="invoicingUtility.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<%-- This portlet resource maps the Action in Base Controller when edit operation is performed in Utilities Invoice grid  --%>
			<portlet:resourceURL var='utilityOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="utilitiesInvoicingGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBUtilities"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
				<%--fix done as a part of release 3.1.2 defect 6420--%>
				<portlet:param name="invoiceId" value="${invoiceId}"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("invoicingUtility.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoicingUtility.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoicingUtility.grid")%></c:set>
			
			<jq:grid id="utilitiesInvoicingGrid-${subBudgetId}" 
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${RateSubGridHeaderRow}"
					 subGridUrl="${loadBudgetRate}"
				     cellUrl="${utilityOperationGrid}"
				     editUrl="${utilityOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName=""
			         isPagination="false"
				     rowsPerPage="0"
			         isSubGrid="true"
			         nonEditColumnName="remainingAmt"
			         positiveCurrency="lineItemInvoiceAmt"
			         checkForTotalValue="lineItemInvoiceAmt,remainingAmt,invoiceAmountEnteredCheck"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
            &nbsp;
            <jsp:include page="programIncomeInvoice.jsp">
					<jsp:param value="3" name="entryTypeId" />
				</jsp:include>
			</c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@4_')}">
		<div id ='4_${subBudgetId}'>
			<H3>OTPS - Professional Services</H3>
	
			<%-- Resource URL to display professional service Grid header row --%>
			<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="invoiceProfessionalServices.grid"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceProfessionalServices.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceProfessionalServices.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceProfessionalServices.grid")%></c:set>
			
			<%-- Portlet resource URL to load data in professional service Grid --%>
			<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="invoiceProfServicesGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBProfessionalServicesBean"/>
				<portlet:param name="gridLabel" value="invoiceProfessionalServices.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<%-- Portlet resource URL for edit professional service Grid data --%>
			<portlet:resourceURL var='invProfServicesGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="invoiceProfServicesGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBProfessionalServicesBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<c:set var="readOnlyStatusProfService">
				<fmt:message key='INVOICE_PENDING_APPROVAL'/>,
				<fmt:message key="INVOICE_APPROVED"/>,
				<fmt:message key="INVOICE_ACTIVE"/>,
				<fmt:message key="INVOICE_CANCELLED"/>,
				<fmt:message key="INVOICE_SUSPENDED"/>,
				<fmt:message key="INVOICE_CLOSED"/>
			</c:set>
			
			<jq:grid id="profServicesGrid-${subBudgetId}" 
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${SubGridHeaderRow}"
					 subGridUrl="${loadGridData}"
				     cellUrl="${invProfServicesGrid}"
				     editUrl="${invProfServicesGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName=""
				     checkForTotalValue="invoiceAmount,remainingAmt,invoiceAmountEnteredCheck" 
			         positiveCurrency="invoiceAmount"
			         isPagination="false"
				     rowsPerPage="5"
			         isSubGrid="true"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
            &nbsp;
            <jsp:include page="programIncomeInvoice.jsp">
					<jsp:param value="4" name="entryTypeId" />
				</jsp:include>
			</c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@5_')}">
		<div id ='5_${subBudgetId}'>
			<c:set var="readOnlyStatusRent">
				<fmt:message key='BUDGET_PENDING_APPROVAL'/>,
				<fmt:message key="BUDGET_APPROVED"/>,
				<fmt:message key="BUDGET_ACTIVE"/>,
				<fmt:message key="BUDGET_CANCELLED"/>,
				<fmt:message key="BUDGET_SUSPENDED"/>,
				<fmt:message key="BUDGET_CLOSED"/>
			</c:set>
			
			<h3>OTPS - Rent</h3>
			<%-- The portlet to display subGridHeaderRow, with the contractInvoiceRent subGrid --%>
			<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractInvoiceRent.grid"/>
			</portlet:resourceURL>
			
			<%-- This is the loadGridData to load the rent grid with the columns specified in contractInvoiceRent Grid --%>
			<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="contractInvoiceRent"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
				<portlet:param name="gridLabel" value="contractInvoiceRent.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<%-- The portlet to display the operation for rent invoice screen such as edit, save cancel  --%>
			<portlet:resourceURL var='RentOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="contractInvoiceRent"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
				<%--fix done as a part of release 3.1.2 defect 6420--%>
				<portlet:param name="invoiceId" value="${invoiceId}"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("contractInvoiceRent.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractInvoiceRent.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractInvoiceRent.grid")%></c:set>
			
			        <jq:grid id="rentInvoicingGrid-${subBudgetId}" 
			       	 isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="{editable:false, editrules:{required:true},editoptions:{maxlength:50}},
			                 {editable:false, editrules:{required:true},editoptions:{maxlength:50}},
			                 {editable:false, editrules:{required:true},editoptions:{maxlength:50}},
			                 {editable:false, editrules:{required:true},edittype : 'select',editoptions : {value : '1:Yes;0:No'}},
			                 {editable:false, editrules:{required:true,number:true,allowOnlyPercentValue},editoptions:{dataInit:function(elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMax:'100.00'});},100);}}},
			                 {editable:false, editrules:{required:true,number:true}},
			                 {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}}"
					 gridUrl="${SubGridHeaderRow}"
					 subGridUrl="${loadGridData}"
				     cellUrl="${RentOperationGrid}"
				     editUrl="${RentOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName=""
			         isPagination="false"
				     rowsPerPage="500"
				     isSubGrid="true"
				     positiveCurrency="lineItemInvoiceAmt"
				     nonEditColumnName="remainingAmt"     
			         checkForTotalValue="lineItemInvoiceAmt,remainingAmt,invoiceAmountEnteredCheck"
			   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
            &nbsp;
            <jsp:include page="programIncomeInvoice.jsp">
					<jsp:param value="5" name="entryTypeId" />
				</jsp:include>
			</c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@6_')}">
		<div id ='6_${subBudgetId}'>
			<H3>OTPS - Contracted Services</H3>
			<div class='formcontainer paymentFormWrapper widthFull'>
				<div class="row">
				      <span class="label">Total Contracted Services Budget:</span>
				      <span class="formfield">
				      	<span class='lftAmount'>
				      		<label id="totCS${subBudgetId}">${invoiceTotalAmountsCS}</label>
				      	</span>
				      </span>
				</div>
				<div class="row">
				      <span class="label">YTD Invoiced Amount:</span>
				      <span class="formfield">
				      	<span class='lftAmount'>
				      		<label id="ytdIA${subBudgetId}">${ytdInvoicedAmountCS}</label>
				      	</span>
				      </span>
				</div>
			</div>
			<portlet:actionURL var="addProcurementUrl" escapeXml="false">
			<portlet:param name="submitAction" value="addProcurement"/>
			</portlet:actionURL>
			
			<div class='clear'>&nbsp;</div>
			
			<%--Consultants Grid Load--%>
			<portlet:resourceURL var='contractedServicesConsultantsSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractedServicesInvoicingConsultants.grid"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("contractedServicesInvoicingConsultants.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractedServicesInvoicingConsultants.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractedServicesInvoicingConsultants.grid")%></c:set>
			
			<portlet:resourceURL var='loadBudgetContractedConsultants' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="getContractedServicesInvoicingConsultants"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
				<portlet:param name="gridLabel" value="contractedServicesInvoicingConsultants.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<%--Consultants Grid Load Ends--%>
			
			<%--JQGrid Mapping for Consultants Grid Operations--%>
			<portlet:resourceURL var='contractedServicesConsultantsOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="getContractedServicesInvoicing"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
				<portlet:param name="subHeader" value="1"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
				<%--fix done as a part of release 3.1.2 defect 6420--%>
				<portlet:param name="invoiceId" value="${invoiceId}"/>
			</portlet:resourceURL>
			<%--JQGrid Mapping for Consultants Grid Operations Ends--%>
			
			<jq:grid id="consultantsGrid-${subBudgetId}" 
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${contractedServicesConsultantsSubGridHeaderRow}"
					 subGridUrl="${loadBudgetContractedConsultants}"
				     cellUrl="${contractedServicesConsultantsOperationGrid}"
				     editUrl="${contractedServicesConsultantsOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName=""
			         isPagination="false"
			         checkForTotalValue="invoiceAmt,remaingAmt,invoiceAmountMoreThanRemaining"
				     rowsPerPage="500"
				     positiveCurrency="invoiceAmt"
			         isSubGrid="true"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"
				     isExpandOnLoad="true"
			/>
			<%--Contracted Services Consultants Grid Ends--%>
			<br>
			<%--SubContractors Grid Load--%>
			<portlet:resourceURL var='contractedServicesSubContractorsSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractedServicesInvoicingSubContractors.grid"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("contractedServicesInvoicingSubContractors.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractedServicesInvoicingSubContractors.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractedServicesInvoicingSubContractors.grid")%></c:set>
			
			<portlet:resourceURL var='loadBudgetContractedSubContractors' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="getContractedServicesInvoicingSubContractors"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
				<portlet:param name="gridLabel" value="contractedServicesInvoicingSubContractors.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<%--SubContractors Grid Load Ends--%>
			
			<%--JQGrid Mapping for SubContractors Grid Operations--%>
			<portlet:resourceURL var='contractedServicesSubContractorsOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="getContractedServicesInvoicing"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
				<portlet:param name="subHeader" value="2"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
				<%--fix done as a part of release 3.1.2 defect 6420--%>
				<portlet:param name="invoiceId" value="${invoiceId}"/>
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
				     columnTotalName=""
			         isPagination="false"
				     checkForTotalValue="invoiceAmt,remaingAmt,invoiceAmountMoreThanRemaining"
				     rowsPerPage="500"
				     positiveCurrency="invoiceAmt"
			         isSubGrid="true"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"
				     isExpandOnLoad="true"
			/>
			<%--Contracted Services Sub-Contractors Grid Ends----%>
			
			<br>
			<%--Vendors Grid Load--%>
			<portlet:resourceURL var='contractedServicesVendorsSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
			<portlet:param name="gridLabel" value="contractedServicesInvoicingVendors.grid"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("contractedServicesInvoicingVendors.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractedServicesInvoicingVendors.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractedServicesInvoicingVendors.grid")%></c:set>
			
			<portlet:resourceURL var='loadBudgetContractedVendors' id='loadGridData' escapeXml='false'>
			<portlet:param name="transactionName" value="getContractedServicesInvoicingVendors"/>
			<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
			<portlet:param name="gridLabel" value="contractedServicesInvoicingVendors.grid"/>
			<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			<%--Vendors Grid Load Ends--%>
			</portlet:resourceURL>
			<%--Vendors Grid Load Ends--%>
			
			<%--JQGrid Mapping for Vendors Grid Operations--%>
			<portlet:resourceURL var='contractedServicesVendorsOperationGrid' id='gridOperation' escapeXml='false'>
			<portlet:param name="transactionName" value="getContractedServicesInvoicing"/>
			<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
			<portlet:param name="subHeader" value="3"/>
			<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			<%--fix done as a part of release 3.1.2 defect 6420--%>
			<portlet:param name="invoiceId" value="${invoiceId}"/>
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
				     columnTotalName=""
			         isPagination="false"
			         checkForTotalValue="invoiceAmt,remaingAmt,invoiceAmountMoreThanRemaining"
				     rowsPerPage="500"
				     positiveCurrency="invoiceAmt"
			         isSubGrid="true"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"
				     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
            &nbsp;
            <jsp:include page="programIncomeInvoice.jsp">
					<jsp:param value="6" name="entryTypeId" />
				</jsp:include>
			</c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@7_')}">
		<div id ='7_${subBudgetId}'>
			<H3>Rate</H3>
			<%-- Below resource url is for rate grid headers --%>
			<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="invoiceRate.grid"/>
			</portlet:resourceURL>
			<%-- Below resource url is for fetching/onload of invoice rate--%>
			<portlet:resourceURL var='loadInvoiceRate' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="invoiceRateGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.RateBean"/>
				<portlet:param name="gridLabel" value="invoiceRate.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<%-- Below resource url is for operations on rate grid --%>
			<portlet:resourceURL var='invoiceRateOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="invoiceRateGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.RateBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
				<%--fix done as a part of release 3.1.2 defect 6420--%>
				<portlet:param name="invoiceId" value="${invoiceId}"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceRate.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceRate.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceRate.grid")%></c:set>
			<jq:grid id="invoiceRateGrid-${subBudgetId}" 
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${RateSubGridHeaderRow}"
					 subGridUrl="${loadInvoiceRate}"
				     cellUrl="${invoiceRateOperationGrid}"
				     editUrl="${invoiceRateOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName=""
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         nonEditColumnName="ytdInvoiceAmt"
			         checkForTotalValue="ytdInvoiceAmt,remainAmt,invoiceAmountEnteredCheck"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     positiveCurrency="ytdInvoiceAmt"
				     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
            &nbsp;
            <jsp:include page="programIncomeInvoice.jsp">
					<jsp:param value="7" name="entryTypeId" />
				</jsp:include>
			</c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>		
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@8_')}">
		<div id ='8_${subBudgetId}'>
			<H3>Milestone</H3>
			
			<portlet:resourceURL var='MilestoneSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="contractInvoiceMilestone.grid"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("contractInvoiceMilestone.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractInvoiceMilestone.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractInvoiceMilestone.grid")%></c:set>
			
			<%-- Below portlet:resourceURL is for Grid load data --%>
			<portlet:resourceURL var='loadInvoiceMilestone' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="getContractInvoiceMilestoneGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBMileStoneBean"/>
				<portlet:param name="gridLabel" value="contractInvoiceMilestone.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<%-- Below portlet:resourceURL is for Grid Edit, Add and Delete operations on data --%>
			<portlet:resourceURL var='milestoneInvoiceOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="getContractInvoiceMilestoneGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBMileStoneBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
				<%--fix done as a part of release 3.1.2 defect 6420--%>
				<portlet:param name="invoiceId" value="${invoiceId}"/>
			</portlet:resourceURL>
			
			<jq:grid id="milestoneInvoiceGrid-${subBudgetId}" 
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${MilestoneSubGridHeaderRow}"
					 subGridUrl="${loadInvoiceMilestone}"
				     cellUrl="${milestoneInvoiceOperationGrid}"
				     editUrl="${milestoneInvoiceOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName=""
			         isPagination="false"
				     rowsPerPage="500"
			         isSubGrid="true"
			         nonEditColumnName="mileStone,remainAmt"
			         checkForTotalValue="invoiceAmount,remainAmt,invoiceAmountEnteredCheck"
			         positiveCurrency="invoiceAmount"
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     isExpandOnLoad="true"
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
            &nbsp;
            <jsp:include page="programIncomeInvoice.jsp">
					<jsp:param value="8" name="entryTypeId" />
				</jsp:include>
			</c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@9_')}">
		<div id ='9_${subBudgetId}'>
			<H3>Unallocated Funds</H3>
			
			<%-- getting the header for the Grid table  --%>
			<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow'
				escapeXml='false'>
				<portlet:param name="gridLabel"
					value="amendment.InvoiceUnallocatedFunds.grid" />
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("amendment.InvoiceUnallocatedFunds.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("amendment.InvoiceUnallocatedFunds.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("amendment.InvoiceUnallocatedFunds.grid")%></c:set>
			
			<%-- loading the page  --%>
			<portlet:resourceURL var='loadGridData' id='loadGridData'
				escapeXml='false'>
				<portlet:param name="transactionName" value="invoiceUnallocatedFunds" />
				<portlet:param name="beanName"
					value="com.nyc.hhs.model.UnallocatedFunds" />
				<portlet:param name="gridLabel"
					value="amendment.InvoiceUnallocatedFunds.grid" />
				<portlet:param name="subBudgetId" value="${subBudgetId}" />
			</portlet:resourceURL>
			
			<%-- operations for Edit/Update/Save --%>
			<portlet:resourceURL var='UnallocatedFundsOperationGrid'
				id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="invoiceUnallocatedFunds" />
				<portlet:param name="beanName"
					value="com.nyc.hhs.model.UnallocatedFunds" />
				<portlet:param name="subBudgetId" value="${subBudgetId}" />
			</portlet:resourceURL>
			
			<%-- JGrid for adding dynamic table --%>
			<jq:grid id="unallocatedFundsInGrid-${subBudgetId}"
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
					rowsPerPage="5" 
					isSubGrid="true"
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
			<input type = 'hidden' value='${subBudgetId}' id='subBudgetId'/>
			<%-- Below resource url is for indirect rate grid headers --%>
			<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
				<portlet:param name="gridLabel" value="invoiceIndirectRate.grid"/>
			</portlet:resourceURL>
			<%-- Below resource url is for fetching/onload of invoice indiirect rate--%>
			<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="indirectInvoicingGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBIndirectRateBean"/>
				<portlet:param name="gridLabel" value="invoiceIndirectRate.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			<%-- Below resource url is for operations on indirect rate grid --%>
			<portlet:resourceURL var='indirectRateOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="indirectInvoicingGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBIndirectRateBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
				<%--fix done as a part of release 3.1.2 defect 6420--%>
				<portlet:param name="invoiceId" value="${invoiceId}"/>
			</portlet:resourceURL>
			<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceIndirectRate.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceIndirectRate.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceIndirectRate.grid")%></c:set>
			
			<h3>Indirect Rate</h3>
			
			<div class="formcontainer paymentFormWrapper widthFull">
				<div class="row">
				      <span class="label">Indirect Rate - City Funded:</span>
				      <span class="formfield">
				      	<span id='indirectRate${subBudgetId}'>${indirectPercentage}</span><span>%</span>
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
				    columnTotalName=""
			        isPagination="false"
				    rowsPerPage="5"
			        isSubGrid="true" 
			        nonEditColumnName="indirectInvoiceAmount" 
			        checkForTotalValue="indirectInvoiceAmount,indirectRemainingAmount,invoiceAmountEnteredCheck" 
			   	    operations="del:false,edit:false,add:false,cancel:false,save:false"
			   	    positiveCurrency="indirectInvoiceAmount"
					isExpandOnLoad="true"			   	     
			/>
			<%-- Added in R7 for Program income grid in budget categories --%>
			<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
            &nbsp;
            <jsp:include page="programIncomeInvoice.jsp">
					<jsp:param value="10" name="entryTypeId" />
				</jsp:include>
			</c:if>
			<%-- R7 changes end --%>
			<div>&nbsp;</div><hr><div>&nbsp;</div>
		</div>
	</c:if>
	
	<c:if test="${fn:contains(tabToShowList, '@###@11_')}">
	<c:if test="${oldPIFlag ne 0}"> 
		<div id ='11_${subBudgetId}'>
			<H3>Program Income</H3>
			
			<%--getting Sub-Grid header  --%>
			<portlet:resourceURL var='programIncomeInvoiceSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
			<portlet:param name="gridLabel" value="programIncomeInvoice.grid"/>
			</portlet:resourceURL>
			
			<c:set var="gridColNames"><%=HHSUtil.getHeader("programIncomeInvoice.grid")%></c:set>
			<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("programIncomeInvoice.grid")%></c:set>
			<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("programIncomeInvoice.grid")%></c:set>
			
			<%-- loading the page  --%>
			<portlet:resourceURL var='loadBudgetProgramIncomeInvoice' id='loadGridData' escapeXml='false'>
				<portlet:param name="transactionName" value="programIncomeInvoiceGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBProgramIncomeBean"/>
				<portlet:param name="gridLabel" value="programIncomeInvoice.grid"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
			</portlet:resourceURL>
			
			<%-- operations for Edit/Update/Save --%>
			<portlet:resourceURL var='programIncomeInvoiceOperationGrid' id='gridOperation' escapeXml='false'>
				<portlet:param name="transactionName" value="programIncomeInvoiceGrid"/>
				<portlet:param name="beanName" value="com.nyc.hhs.model.CBProgramIncomeBean"/>
				<portlet:param name="subBudgetId" value="${subBudgetId}"/>
				<%--fix done as a part of release 3.1.2 defect 6420--%>
				<portlet:param name="invoiceId" value="${invoiceId}"/>
			</portlet:resourceURL>
			
			<%-- JQ Grid Starts--%>
			<jq:grid id="programIncomeInvoiceGrid-${subBudgetId}" 
			         isReadOnly="true"
			         gridColNames="${gridColNames}" 
				     gridColProp="${gridColProp}" 
				     subGridColProp="${subGridColProp}" 
					 gridUrl="${programIncomeInvoiceSubGridHeaderRow}"
					 subGridUrl="${loadBudgetProgramIncomeInvoice}"
				     cellUrl="${programIncomeInvoiceOperationGrid}"
				     editUrl="${programIncomeInvoiceOperationGrid}"
				     dataType="json" methodType="POST"
				     columnTotalName=""
			         isPagination="false"
				     rowsPerPage="7"
			         isSubGrid="true"          
			         positiveCurrency="income"              
				     operations="del:false,edit:false,add:false,cancel:false,save:false"
				     isExpandOnLoad="true"
			/>
			</div>
			</c:if>
			<%-- For new program income screen --%>
		    <c:if test="${oldPIFlag eq 0}">
			<div id='11_${subBudgetId}'>
				<H3>Program Income</H3>
				<%--getting Sub-Grid header  --%>
				<portlet:resourceURL var='programIncomeInvoiceSubGridHeaderRow'
					id='SubGridHeaderRow' escapeXml='false'>
					<portlet:param name="gridLabel"
						value="programIncomeInvoiceNew.grid" />
				</portlet:resourceURL>

				<c:set var="gridColNames"><%=HHSUtil.getHeader("programIncomeInvoiceNew.grid")%></c:set>
				<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("programIncomeInvoiceNew.grid")%></c:set>
				<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("programIncomeInvoiceNew.grid")%></c:set>

				<%-- loading the page  --%>
				<portlet:resourceURL var='loadBudgetProgramIncomeInvoice'
					id='loadGridData' escapeXml='false'>
					<portlet:param name="transactionName"
						value="programIncomeInvoiceGrid" />
					<portlet:param name="beanName"
						value="com.nyc.hhs.model.CBProgramIncomeBean" />
					<portlet:param name="gridLabel"
						value="programIncomeInvoiceNew.grid" />
					<portlet:param name="subBudgetId" value="${subBudgetId}" />
				</portlet:resourceURL>

				<%-- operations for Edit/Update/Save --%>
				<portlet:resourceURL var='programIncomeInvoiceOperationGrid'
					id='gridOperation' escapeXml='false'>
					<portlet:param name="transactionName"
						value="programIncomeInvoiceGrid" />
					<portlet:param name="beanName"
						value="com.nyc.hhs.model.CBProgramIncomeBean" />
					<portlet:param name="subBudgetId" value="${subBudgetId}" />
					<portlet:param name="invoiceId" value="${invoiceId}" />
				</portlet:resourceURL>

				<%-- JQ Grid Starts--%>
				<jq:grid id="newProgramIncomeInvoiceGrid-${subBudgetId}"
					isReadOnly="true" gridColNames="${gridColNames}"
					gridColProp="${gridColProp}" subGridColProp="${subGridColProp}"
					gridUrl="${programIncomeInvoiceSubGridHeaderRow}"
					subGridUrl="${loadBudgetProgramIncomeInvoice}"
					cellUrl="${programIncomeInvoiceOperationGrid}"
					editUrl="${programIncomeInvoiceOperationGrid}" dataType="json"
					methodType="POST" columnTotalName="" isPagination="false"
					rowsPerPage="10" isSubGrid="true" positiveCurrency="income"
					operations="del:false,edit:false,add:false,cancel:false,save:false"
					isExpandOnLoad="true" />
		</div>
		</c:if>
		<div>&nbsp;</div><hr><div>&nbsp;</div>
	</c:if>
	
	<%-- start: Added in R7 for cost center --%>
	
<c:if test="${costCenterOpted eq 2}">
<div id ='11_${subBudgetId}'>
<H3>Services</H3>

<%-- Below resource url is for Equipment grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="InvoiceServices.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("InvoiceServices.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("InvoiceServices.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("InvoiceServices.grid")%></c:set>

<%-- Below resource url is for fetching/onload of equipment grid data--%>
<portlet:resourceURL var='loadServicesGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="servicesInvoiceGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="gridLabel" value="InvoiceServices.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Below resource url is for operations on equipment grid --%>
<portlet:resourceURL var='servicesGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="servicesInvoiceGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<jq:grid id="InvoiceServicesGrid-${subBudgetId}" 
         isReadOnly="true"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadServicesGrid}"
	     cellUrl="${servicesGridActions}"
	     editUrl="${servicesGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName="ytdInvoicedAmt"
         isPagination="true" 
         nonEditColumnName="remainingAmt"
	     rowsPerPage="10"
         isSubGrid="true"
         positiveCurrency="ytdInvoicedAmt"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
	     exportFileName="SERVICES_DETAIL"
/>

<div>&nbsp;</div>

<%-- Below resource url is for Services grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="InvoiceCostCenter.grid"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>


<c:set var="gridColNames"><%=HHSUtil.getHeader("InvoiceCostCenter.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("InvoiceCostCenter.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("InvoiceCostCenter.grid")%></c:set>


<portlet:resourceURL var='loadCostCenterGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="costCenterInvoiceGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="gridLabel" value="InvoiceCostCenter.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>


<portlet:resourceURL var='costCenterGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="costCenterInvoiceGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<H3>Cost center</H3>
<jq:grid id="InvoiceCostCenterGrid-${subBudgetId}" 
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadCostCenterGrid}"
	     cellUrl="${costCenterGridActions}"
	     editUrl="${costCenterGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="10"
         isSubGrid="true"
         positiveCurrency="ytdInvoicedAmt"
         isReadOnly="true"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
	     exportFileName="COST_CENTER_DETAIL"
/> 
	
</div>
</c:if>
<%-- End: Added in R7 for cost center --%>
	
</div>