<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmtMessages" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@ page errorPage="/error/errorpage.jsp"%>
<%--R7 Start: --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%--R7 End --%>
<portlet:resourceURL var='showCBGridTabs' id='showInvoiceGridTabs'
	escapeXml='false'>
	<!-- R6 - Sending this parameter as an identifier for old budget start -->
	<!-- The following attribute is changed from existingBudget to usesFte -->
	<portlet:param name="existingBudget" value="${contractInfo.usesFte}"/>
	<!-- R6 - Sending this parameter as an identifier for old budget end -->
	 <!-- R7 cost center -->
	<portlet:param name="costCenterOpted" value="${contractInfo.costCenterOpted}"/>
</portlet:resourceURL> 

<fmtMessages:setBundle basename="com/nyc/hhs/properties/messages"/>
<input type='hidden' value='${showCBGridTabs}' id='hiddenCBGridTagURL' />
<%--  Start: Added in R7: Program Income --%>
<input type='hidden' value='${contractInfo.oldPIFlag}' id='hiddenIsOldPI' />
<c:if test="${(fn:contains(entryTypeId, '11:1')) or (fn:contains(entryTypeId, '11:0'))}">
<input type='hidden' value='true' id='hiddenIsPiSelected' />
</c:if>
<%--  End R7: Program Income --%>
<style type="text/css">
	
</style>
<style media="print" type="text/css">
	@page 
	{
		size: portrait;
		
	}
	#page 
	{
		width: 80%;
		margin: 0; padding: 0;
		background: none;
	}
    .page-break	{ page-break-before: always; }
    
    .accordionBorder 
    {
    border: none;
	}
	
	.accrodinWrapper 
	{
    	float: inherit;
    	border: 2px solid black;
    	width: 100%;
    	border: 2px solid black;
	}
	.accrodinWrapper h5 
	{
	    color: #fff;
	    float: left;
	    font-size: 1.1em;
	    font-weight: bold;
	    margin: 1.2% 0 1% 1% !important;
	    padding-right: 20px;
	}
	
	.accrodinWrapper ul
	 {
	    display: block;
	    margin-right: 4%;
	    overflow: hidden;
	    padding: 0.8em 0;
	    white-space: nowrap;
	}
	.accContainer
	{
		width: 99.8% !important;
	}
	
	th
	{
		border: 1px solid #a6c9e2;
		background: #dfeffc;
		font-weight: bold !important;
	}
	
	td 
	{
	    border: 1px solid #a6c9e2;
	}
	
	.ui-widget-overlay
	{
		display:none;
		background: transparent;
	}
	
	#alertmod
	{
		display: none;	
	}
	
	* 
	{
		overflow: visible !important;
	}
	
	
	.bodycontainer 
	{
		background: none;
	}
	
	#tabs-container 
	{
	    min-height: 600px;
	    height: auto;
	    border: 2px solid #efefef;
	    overflow: hidden !important; 
	    padding: 8px;
	    width: inherit !important!;
	}
</style>
<script type='text/javascript'>
	// JS objects for ids
	var budgetID = "${aoHashMap.budgetId}";
	var contractID = "${aoHashMap.contractId}";

	var errorMessage = "${errorMessage}";
	var successMessage = "${successMessage}";

	$(document)
			.ready(
					function() {
						$("#invoiceTotal>b").jqGridCurrency();
						$("#assignmentTotal>b").jqGridCurrency();
						$("#advanceRecoup>b").jqGridCurrency();
						$("#totalPayment>b").jqGridCurrency();
						//Fix for defect : 8624 to display returned payment in print invoice
						$("#returnAmt").jqGridCurrency();
						$(function() {
							//pageGreyOutPrintBudget();
							$(function() {
								$("#accordion").accordion();
								/*<c:forEach var="subBudgetData" items="${BudgetAccordianData}" varStatus="subBudgetCounter">
									displayAccordion('#accordionHeaderId${subBudgetData.subBudgetID}');
									showCBGridTabsJSP('contractInvoicePrintSummary','invoiceSummary${subBudgetData.subBudgetID}','${subBudgetData.subBudgetID}','${contractInfo.invoiceId}','${contractInfo.budgetId}');
								</c:forEach>
								displayAccordion('#accordionHeaderId3');
								showCBGridTabsJSP('invoiceAssignmentsPrint', 'assignmentWrapper', '','${contractInfo.invoiceId}','${contractInfo.budgetId}');
								displayAccordion('#accordionHeaderId2');
								showCBGridTabsJSP('invoiceAdvancesPrint', 'advanceWrapper', '','${contractInfo.invoiceId}','${contractInfo.budgetId}');*/
							});
						});
					});
	
	$(document).ajaxStop(function() {
		removePageGreyOutPrintBudget();
	});
	
	// function to fetch details : this further calls onload and grid operation
	function showCBGridTabsJSP(tabName, tabId, subbudgetId, invoiceId,budgetId) {
		pageGreyOutPrintBudget();
		var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
				+ subbudgetId + "&hdnInvoiceId=" + invoiceId+ "&budgetID=" + budgetId + "&tabToShowList=" + showSelectedEntiTypeTabs()
				+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
		if ('${readOnlyPageAttribute}' != "false") {
			v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
					+ subbudgetId + "&hdnInvoiceId=" + invoiceId + "&readOnlyPage=true" + "&tabToShowList=" + showSelectedEntiTypeTabs()
					+ "&budgetID=" + budgetId // Fix for defect : 8624 to display returned payment in print invoice
					+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
		}
		var urlAppender = $("#hiddenCBGridTagURL").val();
		
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				//fix done as a part of release 3.1.2 defect 6420
				if(e==null || e =='')
					{
					redirectTo = $("#contextPathSession").attr("value")+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=invoiceListAction&fromMultipleInvoice=true";
					location.href= redirectTo;
					}
				else
					{
				$("#" + tabId).html(e);
				//removePageGreyOut();
					}
			},
			beforeSend : function() {
			}
		});
	}
	
	function showSelectedEntiTypeTabs(){
		var entryTypeData = '${entryTypeId}';
		var tabToShowList = '@###@';
		entryTypeData = entryTypeData.replace('[','').replace(']','').split(',');
		if(entryTypeData != '')
			for(var i=0; i<entryTypeData.length; i++)
			{
				//For R4 Contract Budget
				tabToShowList = tabToShowList + '@###@' + $.trim(entryTypeData[i]).split(':')[0]+'_'+ '@###@';
			}
			else
				for(var i=0; i<12; i++)
				{
					//For R3 Contract Budget
					tabToShowList = tabToShowList + '@###@' + (i+1)+'_'+ '@###@';
				} 
		return tabToShowList;
	}
</script>

<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
	<h2 class='autoWidth'>Contract Invoicing</h2>
	<div class='hr'></div>
	<% String lsErrorMessage = "";
		if(null != request.getAttribute("errorMessage")){
			lsErrorMessage = (String) request.getAttribute("errorMessage");
		%>
		<input type="text"/>
			<div class="failed breakAll" style="display:block" id="error"><%=lsErrorMessage%></div>			
	<%} %>	
	
	<div class='floatRht'>Status: ${contractInfo.budgetStatus}</div>
	<div class='clear'></div>

	<%-- Container Starts --%>
	<%-- Form Data Starts --%>
	<h3>Contract Information</h3>
	<div class="formcontainer paymentFormWrapper">
		<div class="row">
			<span class="label">Agency:</span>
			<span class="formfield">${contractInfo.contractAgencyName}</span>
		</div>
		<div class="row">
			<span class="label">Procurement/Contract Title:</span>
			<span class="formfield">${contractInfo.contractTitle}</span>
		</div>
		<div class="row">
			<span class="label">Provider:</span>
			<span class="formfield">${contractInfo.provider}</span>
		</div>
		<div class="row">
			<span class="label">Procurement E-PIN:</span>
			<span class="formfield">${contractInfo.procEpin}</span>
		</div>
		<div class="row">
			<span class="label">Award E-PIN:</span>
			<span class="formfield">${contractInfo.awardEpin}</span>
		</div>
	</div>

	<div class="formcontainer paymentFormWrapper">
		<div class="row">
			<span class="label">CT#:</span> 
			<span class="formfield"> ${contractInfo.extCT} </span>
		</div>
		<div class="row">
			<span class="label">Contract Start Date:</span>
			<span class="formfield"> <fmt:formatDate pattern="MM/dd/yyyy" value="${contractInfo.contractStartDate}" /></span>
		</div>
		<div class="row">
			<span class="label">Contract End Date:</span>
			<span class="formfield"> <fmt:formatDate pattern="MM/dd/yyyy" value="${contractInfo.contractEndDate}" /> </span>
		</div>
		<div class="row">
			<span class="label">Contract Amount:</span>
			<span class="formfield"> <fmt:formatNumber type="currency" value="${contractInfo.contractValue}" /> </span>
		</div>
		<div class="row">
			<span class="label">Program Name:</span>
			<span class="formfield">${contractInfo.programName}</span>
		</div>
	</div>

	<p>&nbsp;</p>

	<%-- Fiscal Year Budget Information Starts --%>
	<h3>Fiscal Year Budget Information</h3>
	<div class='tabularWrapper'>
		<table cellspacing="0" cellpadding="0" class="grid">
			<tbody>	
				<tr>
					<th>Start Date</th>
					<th>End Date</th>
					<th>FY Budget</th>
					<th>YTD Invoiced Amount</th>
					<th>Remaining Amount</th>
					<th>YTD Actual Paid Amount</th>
					<th>Cash Balance</th>
				</tr>
				<tr>
					<td><fmt:formatDate pattern="MM/dd/yyyy"
						value="${fiscalBudgetInfo.startDate}" /></td>
					<td><fmt:formatDate pattern="MM/dd/yyyy"
						value="${fiscalBudgetInfo.endDate}" /></td>
					<td><fmt:formatNumber type="currency"
						value="${fiscalBudgetInfo.approvedBudget}" /></td>
					<!-- Start: Updated in R7 for Defect 8878 -->
					<td><fmt:formatNumber type="currency"
						value="${fiscalBudgetInfo.ytdInvoicedAmount}" /></td>
					<!-- End: Updated in R7 for Defect 8878 -->
					<td><fmt:formatNumber type="currency"
						value="${fiscalBudgetInfo.remainingAmount}" /></td>
					<!-- Start: Updated in R7 to make consistent with Contract Invoice Page-->
					<td><fmt:formatNumber type="currency"
						value="${fiscalBudgetInfo.ytdActualPaid}" /></td>
					<!-- Start: Updated in R7 -->
					<td><fmt:formatNumber type="currency"
						value="${fiscalBudgetInfo.cashBalance}" /></td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<p>&nbsp;</p>
	
	<%-- Invoice Information Starts --%>		
	<h3>Invoice Information</h3>
	<div class="formcontainer paymentFormWrapper">
		<div class="row">
		  <span class="label">Invoice Number:</span>
		  <span class="formfield">${invoiceInfo.invoiceNumber}</span>
		</div>
		<div class="row">
		  <span class="label">Provider Invoice Number:</span>
		  <span class="formfield">
		  	<input name="prvInvNum" id="prvInvNum" maxlength="20" type="text" value="${invoiceInfo.invoiceProvider}" disabled="disabled"/>
		  </span>
		</div>
		<div class="row">
		  <span class="label">Service Date From:</span>
		  <span class="formfield">${invoiceInfo.invoiceStartDate}</span>
		</div>
		<div class="row">
		  <span class="label">Invoice Submission Date:</span>
		  <span class="formfield">
	  			<c:if test="${contractInfo.budgetStatus ne 'Pending for submission'}">
		  			${invoiceInfo.invoiceDateSubmitted}
	  		  	</c:if>
				<c:if test="${contractInfo.budgetStatus eq 'Pending for submission'}">
				  	N/A
				</c:if>
		  </span>
		</div>
	</div>
	
	<div class="formcontainer paymentFormWrapper">
		<div class="row">
		  <span class="label clearLabel">&nbsp;</span>
		  <span class="formfield"></span>
		</div>
		<div class="row">
		  <span class="label">Agency Invoice Number:</span>
		  <span class="formfield">
		  	<input name="invoiceNumber" maxlength="20" type="text" value="${invoiceInfo.agency}" disabled="disabled"/>
		  </span>
		</div>
		<div class="row">
		  <span class="label">Service Date To:</span>
		  <span class="formfield">${invoiceInfo.invoiceEndDate}</span>
		</div>
		<div class="row">
		  <span class="label">Invoice Approved Date:</span>
		  <span class="formfield">
			<c:choose>
		  		<c:when test="${contractInfo.budgetStatus eq 'Approved'}">
		  			${invoiceInfo.invoiceDateApproved}
		  		</c:when>
		  		<c:otherwise>
		  			N/A
		  		</c:otherwise>
		  </c:choose>
		  </span>
		</div>
	</div>
	
	<p>&nbsp;</p>	
		
	<div class="tabularWrapper" id="assignAdvanceTable"> 
		<table cellspacing="0" cellpadding="0" border='1' style='width:50%; margin:auto; float:none'> 
		   <tbody>
			   <tr>
					<th class='right'>Description</th>
					<th class='right'>Amount</th>
			  </tr>                
			  <tr>
				<td><label><b>Invoice Total</b></label></td>
				 <td><label id="invoiceTotal"><b>${invoiceInfo.invoiceValue}</b></label></td>
			  </tr>
			 <tr>
				<td><label>Assignment Total</label></td>
				<td><label><b><label id="assignmentTotal"><b>${invoiceInfo.assignmentValue}</b></label></label></td>
			  </tr>
			  <tr>
				<td><label>Advance Recoupment Total</label></td>
				<td><label id="advanceRecoup"><b>${invoiceInfo.advanceValue}</b></label></td>
			  </tr>
			   <tr>
				<td><label><b>Total Proposed Payment to Vendor</b></label></td>
				 <td><label id="totalPayment"><b>${invoiceInfo.totalValue}</b></label></td>
			  </tr>
			</tbody>
		</table>
	</div>
	<p>&nbsp;</p>
	<form:form action="" method="post" name="invoiceFormPrint" id="invoiceFormPrint">
		<c:forEach var="subBudgetData" items="${BudgetAccordianData}" varStatus="subBudgetCounter">
			<div id="accordionTopId">
				<div class="accrodinWrapper hdng" id="accordionHeaderId${subBudgetData.subBudgetID}"
					onclick="displayAccordion(this);if(divEmpty('invoiceSummary${subBudgetData.subBudgetID}')){showCBGridTabsJSP('contractInvoicePrintSummary','invoiceSummary${subBudgetData.subBudgetID}','${subBudgetData.subBudgetID}','${contractInfo.invoiceId}','${contractInfo.budgetId}');}">
					<h5 class="breakAll">${subBudgetData.subBudgetName}</h5>
					<ul>
						<li>
							<label> <fmt:formatNumber type="currency" value="${subBudgetData.subBudgetAmount}" /></label>
						</li>
					</ul>
				</div>
				<input type="hidden" value="${subBudgetCounter.count}" id="subBudgetCounterCount${subBudgetCounter.count}">  
				<input type="hidden" value="${subBudgetData.subBudgetID}" id="subBudgetDataSubBudgetID${subBudgetCounter.count}">
				<div id="accordianId${subBudgetData.subBudgetID}" class="close">
					<div class="accContainer">
						<div id="tabs${subBudgetData.subBudgetID}" class='accordionBorder'>
							<ul class='procurementTabber'></ul>
							<div class="clear accordionWrapper">
								<div id='invoiceSummary${subBudgetData.subBudgetID}'>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:forEach>
	</form:form>
	<div class="page-break"></div>
	<div id="accordionTopId2">
		<div class="accrodinWrapper hdng" id="accordionHeaderId2" onclick="displayAccordion(this);if(divEmpty('advanceWrapper')){showCBGridTabsJSP('invoiceAdvancesPrint', 'advanceWrapper', '','${contractInfo.invoiceId}','${contractInfo.budgetId}');}">
			<h5>Advances</h5>
			<ul>
				<li><label>&nbsp;</label>
				</li>
			</ul>
		</div>
		<div id="accordianId2" class="close">
			<div id='tabs-container' class="clearHeight">
				<div class="accContainer" id="advanceWrapper"></div>
			</div>
		</div>
	</div>
	<div id="accordionTopId3">
		<div class="accrodinWrapper hdng" id="accordionHeaderId3" onclick="displayAccordion(this);if(divEmpty('assignmentWrapper')){showCBGridTabsJSP('invoiceAssignmentsPrint', 'assignmentWrapper', '','${contractInfo.invoiceId}','${contractInfo.budgetId}');}">
			<h5>Assignments</h5>
				<ul>
					<li><label>&nbsp;</label>
					</li>
				</ul>
		</div>
		<div id="accordianId3" class="close">
			<div id='tabs-container' class="clearHeight">
				<div class="accContainer" id="assignmentWrapper"></div>
			</div>
		</div>
		<div>&nbsp;</div>
	</div>
	<!-- Fix for defect : 8624 to display returned payment in print invoice-->
	<c:if test="${contractInfo.noOfReturnedPayments > 0}">
	<div id="accordionTopId4">
		<div class="accrodinWrapper hdng" id="accordionHeaderId4" onclick="displayAccordion(this);if(divEmpty('returnedPaymentWrapper')){showCBGridTabsJSP('returnedPaymentPrintView', 'returnedPaymentWrapper', '','','${contractInfo.budgetId}');}">
			<h5>Returned Payments</h5>
			<ul>
				<li><label id="returnAmt" class='accordionDollar'>${contractInfo.returnedPaymentAmount}</label></li>
			</ul>
		</div>
		<div id="accordianId4" class="close">
			<div id='tabs-container' class="clearHeight">
				<div class="accContainer" id="returnedPaymentWrapper"></div>
			</div>
		</div>
	</div>
	</c:if>