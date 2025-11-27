<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmtMessages" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%--R7 Start: --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%--R7 End --%>
<portlet:resourceURL var='showCBGridTabs' id='showCBGridTabs'
	escapeXml='false'>
	<!-- R6 - Sending this parameter as an identifier for old budget start -->
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
    	float: none;
    	border: 2px solid black;
    	width: 98%;
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
		width: 98%;
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
	
	.tabularWrapper table 
	{
    	width: 96.9% !important;
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
						$(function() {
							//pageGreyOutPrintBudget();
							$("#accordion").accordion();
							/*$(function() {
								<c:forEach var="subBudgetData" items="${BudgetAccordianData}" varStatus="subBudgetCounter">
								showCBGridTabsJSP('contractBudgetPrintSummary','budgetSummary'+'${subBudgetData.subBudgetID}','${subBudgetData.subBudgetID}');	
								displayAccordion('#accordionHeaderId${subBudgetData.subBudgetID}');
								</c:forEach>
							});*/
						});
					});
	
	$(document).ajaxStop(function() {
		removePageGreyOutPrintBudget();
	});
	
	//This function is called on click of view Comments History tab on task footer
	//Updated in R7 :Added hiddenIsOldPI and hiddenIsPiSelected for Program Income
	function showCBGridTabsJSP(tabName, tabId, subbudgetId) {
		pageGreyOutPrintBudget();
		var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
				+ subbudgetId + "&tabToShowList=" + showSelectedEntiTypeTabs()+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()
				+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
		var urlAppender = $("#hiddenCBGridTagURL").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#" + tabId).html(e);
				$(".linkPrint").hide();
				//removePageGreyOut();
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
				//$('#'+$.trim(entryTypeData[i]).split(':')[0]+'_'+_counter[j]).show();
				tabToShowList = tabToShowList + '@###@' + $.trim(entryTypeData[i]).split(':')[0]+'_'+ '@###@';
			}
			else
				for(var i=0; i<12; i++)
				{
					//For R3 Contract Budget
					//$('#'+(i+1)+'_'+_counter[j]).show();
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
	<h2 class='autoWidth'>Contract Budget</h2>
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
			<tr>
				<th>Start Date</th>
				<th>End Date</th>
				<th>FY Budget</th>
				<th>YTD Invoiced Amount</th>
				<th>Remaining Amount</th>
				<th>YTD Actual Paid Amount</th>
				<th>Unrecouped Advance Amount</th>
			</tr>
			<tr>
				<td><fmt:formatDate pattern="MM/dd/yyyy"
					value="${fiscalBudgetInfo.startDate}" /></td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"
					value="${fiscalBudgetInfo.endDate}" /></td>
				<td><fmt:formatNumber type="currency"
					value="${fiscalBudgetInfo.approvedBudget}" /></td>
				<td><fmt:formatNumber type="currency"
					value="${fiscalBudgetInfo.invoicedAmount}" /></td>
				<td><fmt:formatNumber type="currency"
					value="${fiscalBudgetInfo.remainingAmount}" /></td>
				<td><fmt:formatNumber type="currency"
					value="${fiscalBudgetInfo.ytdActualPaid}" /></td>
				<td><fmt:formatNumber type="currency"
					value="${fiscalBudgetInfo.unRecoupedAmount}" /></td>
			</tr>
		</table>
	</div>
	<p>&nbsp;</p>
	<form:form action="" method="post" name="contractBudgetFormPrint" id="contractBudgetFormPrint">
		<c:forEach var="subBudgetData" items="${BudgetAccordianData}" varStatus="subBudgetCounter">
			<div id="accordionTopId">
				<div class="accrodinWrapper hdng" id="accordionHeaderId${subBudgetData.subBudgetID}"
				onclick="displayAccordion(this);if(divEmpty('budgetSummary${subBudgetData.subBudgetID}')){showCBGridTabsJSP('contractBudgetPrintSummary','budgetSummary${subBudgetData.subBudgetID}','${subBudgetData.subBudgetID}','');}">
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
								<div id='budgetSummary${subBudgetData.subBudgetID}'>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:forEach>
	</form:form>