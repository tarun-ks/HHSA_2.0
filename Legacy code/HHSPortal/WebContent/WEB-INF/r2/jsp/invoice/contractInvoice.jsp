<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%--R7 Start: --%>
<%@taglib prefix="im" uri="/WEB-INF/tld/informationMessage.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%--R7 End --%>
<%@ page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page import="com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page import="javax.portlet.PortletSession"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSConstants"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<fmt:setLocale value="en_US"/>
<portlet:defineObjects />
<%-- The Contract Invoice page gives an overview of the contract information,
 fiscal year budget information, and allows the user to drill down into more
  specific views of the budget; for example, line item details for personnel
   services, OTPS, etc   Provider users will be able to view and upload
    documents to support an invoice  
    The page contains the following sections
    Contract Information : Contains general information about the contract such as 
    			Procurement name contract value startend date etc 
	Fiscal Year Budget Information : Contains general information about the Fiscal Year
		 Budget such as the fiscal year amount, available balance, etc
	Invoice Information : Contains some general information about the current 
		invoice such as invoice numbers ,system generated and allows for Agency and Provider 
		proprietary invoice numbers to be created, service dates, and transaction totals 
		for the invoice.Invoice total less Assignments and Recoupments.
    Budget : Each contract can have more than one budget per fiscal year. 
 		Depending on the number of budgets which were setup during the Contract Configuration task, 
 			an expandable or collapsible section will be created for each of those budgets.
	Documents: the Provider will be able to upload and view documents
		 associated with the budget in this section.
	Advance Summary : In this section, the user will be able to view Advance 
		information such as advance amount and advance amount recouped to date for each approved advance.
	Assignments Summary : The Provider/Agency user will be able to view the
		 assignees selected for this budget and the relevant fiscal information. 
		  Assignments can only be added during the invoicing process.
	Comments or Comments History : The Provider user can enter comments for
		 the Agency to view when reviewing the invoice submitted by the Provider.
		   The Provider will also be able to see a history of comments previously 
		   submitted and those comments given by the Agency.
    --%>

 <c:choose>
<%-- Start changes for R5 --%>
<c:when test="${(org_type ne 'provider_org') or ((org_type eq 'provider_org') and (contractInfo.providerOrgId eq user_organization) and (contractInfo.contractAccess))}">
<%-- End changes for R5 --%>
<%-- This portlet resource is for Confirmation Popup mapping --%>
<portlet:resourceURL var="invoiceSubmissionOverlay" id="invoiceSubmissionOverlay" escapeXml="false" />
<input type="hidden" id="invoiceSubmissionOverlayVar" value="${invoiceSubmissionOverlay}"/>
<input type='hidden' value='${org_type}' id='hiddenLoggedInOrgType' />
<%--  Start: Added in R7: Program Income --%>
<input type='hidden' value='${contractInfo.oldPIFlag}' id='hiddenIsOldPI' />
<c:if test="${(fn:contains(entryTypeId, '11:1')) or (fn:contains(entryTypeId, '11:0'))}">
<input type='hidden' value='true' id='hiddenIsPiSelected' />
</c:if>
<%--  End R7: Program Income --%>
<%--  Start: Added in Defect-8505 --%>
<%-- The following attribute is changed from existingBudget to usesFte --%>
<input type="hidden" name="existingBudget" id="existingBudget" value="${contractInfo.usesFte}"/>
<%--  Start: Added in Defect-8505 --%>
<%-- This portlet resource for review level validation --%>
<portlet:resourceURL var="invoiceStatusValidation" id="invoiceStatusValidation" escapeXml="false" />
<input type="hidden" id="invoiceStatusValidationVar" value="${invoiceStatusValidation}"/>

<%-- This portlet resource for Confirmation Popup mapping ends --%>
<portlet:resourceURL var='showCBGridTabs' id='showInvoiceGridTabs'
	escapeXml='false'>
</portlet:resourceURL>
<portlet:resourceURL var="saveContractInvoice" id="saveContractInvoice" escapeXml="false">
</portlet:resourceURL>

<portlet:resourceURL var='getCallBackContractInvoiceData' id='getCallBackContractInvoiceData' escapeXml='false'>
</portlet:resourceURL>
<portlet:resourceURL var='getCallBackContractBudgetData' id='getCallBackContractBudgetData' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${getCallBackContractInvoiceData}' id='getCallBackContractInvoiceData'/>
<input type = 'hidden' value='${getCallBackContractBudgetData}' id='getCallBackContractBudgetData'/>
<%-- logic for security implementation --%>
<input type="hidden" name="saveContractInvoice" id="saveContractInvoiceUrl" value="${saveContractInvoice}" />
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if
	test="${(contractBudgetReadonly ne null && contractBudgetReadonly eq 'true') || org_type eq 'agency_org' || org_type eq 'city_org' ||(accessScreenEnable eq false) }">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<!-- start -->
<portlet:actionURL var="navigateToContractInvoiceURL" escapeXml="false">
<portlet:param name="launchInvoice" value="contractInvoiceScreen"/>
<portlet:param name="action" value="invoiceListAction"/>
</portlet:actionURL>
<input type='hidden' value="${navigateToContractInvoiceURL}" id="navigateToContractInvoiceURL"/>
<input type='hidden' value='${showCBGridTabs}' id='hiddenCBGridTagURL' />
<input type='hidden' value='${contractInfo.budgetStatus}' id='hiddenTaskStatus' />
<%--  Start: Added in Defect-8470 --%>
<input type='hidden' value='' id='hiddenReadOnlyPageAttribute' />
<%--  End: Added in Defect-8470 --%>
<%--Start : Added in R5 --%>
<portlet:actionURL var='navigateListScreenUrl' escapeXml='false'>
	<portlet:param  name='controllerAction' value='redirectFromLanding'/>
</portlet:actionURL>
<input type="hidden" name="hiddenNavigateListScreenUrl" id="hiddenNavigateListScreenUrl"  value="${navigateListScreenUrl}"/>
<%--End : Added in R5 --%>


<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractInvoice.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/autoNumeric-1.7.5.js"></script>


<script>
var serviceDateFrmNotInRange = "<fmt:message key='SERVICE_DATE_FRM_NOT_IN_RNG'/>";
var serviceDateToNotInRange = "<fmt:message key='SERVICE_DATE_TO_NOT_IN_RNG'/>";
var serviceDateFromBeforeDateTo = "<fmt:message key='SERVICE_DATE_FRM_BFR_SRVC_DATE_TO'/>";

var serviceDateFromInvalid = "<fmt:message key='SERVICE_DATE_FROM'/>";
var serviceDateToInvalid = "<fmt:message key='SERVICE_DATE_TO'/>";


    $(function() {
        $('#invoiceComments li, #tabs li').removeClass('ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
		$( "#invoiceComments, #tabs" ).tabs();		
    });
    </script>

<script type='text/javascript'>
var budgetID = "";
	/**
	* function to fetch details : this further calls onload and grid operation
	* Updated in R6 : Returned Payment, added budget id in parameters 
	* Updated  in R7 : Added hiddenIsOldPI for Program Income
	**/
function showCBGridTabsJSP(tabName, tabId, subbudgetId, invoiceId,budgetId) {
	pageGreyOut();
	var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
			+ subbudgetId + "&hdnInvoiceId=" + invoiceId+ "&budgetID=" + $("#budgetId").val()+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()
			+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
			
	if ('${readOnlyPageAttribute}' != "false") {
		v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
				+ subbudgetId + "&hdnInvoiceId=" + invoiceId + "&readOnlyPage=true" + "&budgetID=" + $("#budgetId").val()
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
			//	code updation for R4 starts
			if(tabName == "invoiceSummary" && $("#hiddenLoggedInOrgType").val() == 'provider_org'){
				highlightProviderTabsWithAgencyCommentsInvoice(tabId, subbudgetId);
			}
			//	code updation for R4 ends
			removePageGreyOut();
				}
		},
		beforeSend : function() {
		}
	});
}

//R4: Tab Level Comments - This function highlights the Line Item Tabs that have Agency Comments
function highlightProviderTabsWithAgencyCommentsInvoice(tabId, subbudgetId)
{
	if ($("#hiddenTaskStatus").val()=="Returned for Revision")
	{
		subBudgetCounterNum = tabId.split('invoiceSummary');
		var subBudgetHighlightTabs = $("input[name=hdnTabHighlightList"+subbudgetId+"]").val();
		subBudgetHighlightTabs = subBudgetHighlightTabs.substring(1,subBudgetHighlightTabs.length-1);
		subBudgetHighlightTabsList = subBudgetHighlightTabs.split(', ');
		for(var count = 0; count<subBudgetHighlightTabsList.length; count++)
			$('#'+subBudgetHighlightTabsList[count]+'_'+subBudgetCounterNum[1]+' a').addClass('highlightTlcTabs');
	}
}

$(document)
.ready(
		function() {
			$(function() {
				budgetID = $("#budgetId").val();
				//Start :Added in Defect-8470
				$('#hiddenReadOnlyPageAttribute').val('${readOnlyPageAttribute}');
				//End :Added in Defect-8470
				$("#contractVal").jqGridCurrency();
				$("#accordion").accordion();
				$("#returnAmt").jqGridCurrency();
				<c:forEach var="subBudgetData"   items="${BudgetAccordianData}" varStatus="subBudgetCounter"  >
				$("#subBudAmt${subBudgetCounter.count}").jqGridCurrency();
				$('#tabs${subBudgetCounter.count} li')
						.removeClass(
								'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
				$('#tabs${subBudgetCounter.count}').tabs();
				</c:forEach>
			});
			
			//Starts Logic For EntityTypeTab Show 
			showSelectedEntiTypeTabs();
			//Ends Logic For EntityTypeTab Show
			
			$("#documentWrapper").html( $("#tempDocument").html());
			$("#tempDocument").html("");
		  if('${readOnlyPageAttribute}' != "false"){
		       $("#taskButtonsId").hide();
		   }		
			
		});
//	code updation for R4 starts
//This function is called by default on page load.
function showSelectedEntiTypeTabs(){
	var _counter = null;
	<c:forEach var="subBudgetData" items="${BudgetAccordianData}" varStatus="subBudgetCounter">
		if(_counter != null){
			_counter = _counter + ',' + '${subBudgetCounter.count}';
		}else{
			_counter = '${subBudgetCounter.count}';
		}
		for(var _count=1; _count<=11; _count++)
			$('#'+_count+'_'+'${subBudgetCounter.count}').hide();
	</c:forEach>
	_counter=_counter.split(',');
	var entryTypeData = '${entryTypeId}';
	entryTypeData = entryTypeData.replace('[','').replace(']','').split(',');
	for(var j=0; j<_counter.length; j++)
		if(entryTypeData != '')
		for(var i=0; i<entryTypeData.length; i++)//For R4 Contract Budget
			$('#'+$.trim(entryTypeData[i]).split(':')[0]+'_'+_counter[j]).show();
		else
			for(var i=0; i<12; i++) //For R3 Contract Budget
				$('#'+(i+1)+'_'+_counter[j]).show();
}
//code updation for R4 ends

/**
* This method is added in Release 6.This function is call on click of grid tabs to show the Personnel Services tab.
* This method is moved from invoicePersonnelServicesTab.jsp as part of defect 8465.
**/
function showPsSCreen(tabName, tabId, subbudgetId, invoiceId,budgetId) {
	pageGreyOut();
	var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
			+ subbudgetId + "&hdnInvoiceId=" + invoiceId+ "&budgetID=" + budgetId
			+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
			
	if ($('#hiddenReadOnlyPageAttribute').val() != "false") {
		v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
				+ subbudgetId + "&hdnInvoiceId=" + invoiceId + "&readOnlyPage=true"
				+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
	}
	var urlAppender = $("#hiddenCBGridTagURL").val();
	
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#personnelServiceTab_" + subbudgetId).html(e);
			removePageGreyOut();
		},
		beforeSend : function() {
		}
	});
}
</script>
  

<style type="text/css">

.alert-box-submit-invoice .ui-state-active {
	background: #4297E2 !important
}
.alert-box-submit-invoice {
	background: #FFF;
	display: none;
	z-index: 1001;
	position: fixed
}

/* Fixed for Contract information as per updated wireframes......page specific fix, DO NOT INCLUDE IN EXTERNAL CSS */
.formcontainer .row span.vendorfield{
	float: left;
	min-height: 20px;
	padding: 4px 0;
	text-align: left;
	width:60%
}
</style>

<%-- checking the transaction success or failure --%>
<c:set var="sectionSubmitButton"><%=HHSComponentMappingConstant.PROVIDER_S329_PAGE_SUBMIT%></c:set>

<d:content section="<%=ComponentMappingConstant.HEADER_AGENCYF%>"  isReadOnly="${readOnlyPageAttribute}" authorize="">
<form:form action="" method="post" name="contractBudgetForm"
		id="contractBudgetForm"> 
		<c:if test="${accessScreenEnable eq false}">
			<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record will be displayed in Read-only format.</div>
		</c:if>
		<%
		String lsTransactionMsg = "";
		if (null!=request.getAttribute("transactionStatus") && "success".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){
			lsTransactionMsg = (String)request.getAttribute("transactionMessage");
		%>
			<div id="transactionStatusDiv" class="passed" style="display: block"><%=lsTransactionMsg%></div>
		<%}%>
		
		<% String lsErrorMessage = "";
		if(null != request.getAttribute("errorMessage")){
			lsErrorMessage = (String) request.getAttribute("errorMessage");
		%>
			<div class="failed breakAll" style="display:block" id="error"><%=lsErrorMessage%></div>			
	<%} %>	
<div id="transactionStatusDiv" class=""></div>
	
<input type="hidden" id="budgetId" value="${contractInfo.budgetId}" />
<input type="hidden" id="contractId" value="${contractInfo.contractId}" />
<input type="hidden" id="invoiceId" value="${contractInfo.invoiceId}" />



<div class="failed" id="errorGlobalMsg"></div>
<div class="passed" id="successGlobalMsg"></div>
<%-- Code need to Integrate Starts --%>
<h2><label class='floatLft'>Contract Invoicing</label>
<div class="linkReturnValut floatRht">
	<a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=invoiceListAction" title="Return to Invoice List"">Return to Invoice List</a>
</div>
</h2>
<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		    <d:content section="${helpIconProvider}">
		     <div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
		          <input type="hidden" id="screenName" value="Invoice" name="screenName"/>
		   </d:content> 	
	<div class='hr'></div>
	<div class='floatRht'>Status: ${contractInfo.budgetStatus}</div>
	<div class='clear'></div>
	<!-- Added for Release 3.4.0, #5681 - Adding a Print Link Starts -->
	<span class='linkPrint floatRht'><a onclick="PrintView()" class='link' title='Print Budget'>Print Invoice</a></span>
	<div class='clear'></div>
	<a style="display:none" href= "<portlet:renderURL><portlet:param name='render_action' value='printInvoice'/>
	<portlet:param name='contractId' value="${contractInfo.contractId}"/>
	<portlet:param name='budgetId' value="${contractInfo.budgetId}"/>
	<portlet:param name='invoiceId' value="${contractInfo.invoiceId}"/>
	</portlet:renderURL>"  class='printerViewCB' id="printInvoice"></a>
	<!-- Added for Release 3.4.0, #5681 - Adding a Print Link Ends -->
	<%-- R7 Start: Added for contract level message--%>
	<c:if test="${org_type eq 'agency_org' || org_type eq 'city_org'}">  
	<im:message contractId="${contractInfo.contractId}"/>
	</c:if>
	<%-- R7 End:--%>
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
		<div class="row"><span class="label"
			title="The CT# is the contract registration number issued by the City's Financial Management System (FMS). You can use this number to search for additional information in the FMS' Payee Information Portal - https://nyc.gov/pip">CT#:</span>
		<span class="formfield"> ${contractInfo.extCT} </span></div>
		<div class="row">
			<span class="label">Contract Start Date:</span>
			<span class="formfield"> 
				<fmt:formatDate pattern="MM/dd/yyyy" value="${contractInfo.contractStartDate}" /> </span>
		</div>
		<div class="row">
			<span class="label">Contract End Date:</span> 
			<span class="formfield"> 
				<fmt:formatDate pattern="MM/dd/yyyy" value="${contractInfo.contractEndDate}" /> </span>
		</div>
		<div class="row">
			<span class="label">Contract Amount:</span> 
			<span class="formfield" id="contractVal">${contractInfo.contractValue} 
			</span>
		</div>
		<div class="row">
			<span class="label">Program Name:</span> 
			<span class="formfield">${contractInfo.programName}</span>
		</div>
	</div>

	<p>&nbsp;</p>
	<%-- Contract Information Ends --%>


<p>&nbsp;</p>

<%-- Fiscal Year Budget Information Grid Starts  --%>
<div id="assignAdvanceId">
<jsp:include
			page="/WEB-INF/r2/jsp/invoice/invoiceAssignAdvanceTable.jsp" />
 </div>
<!-- Start : R5 Added -->
<span class="relatedInfoLinks">
 	View Related:&nbsp;&nbsp;<a class="activelink" href="javascript:void(0);" onclick="submitFormToViewContractList('${contractInfo.contractId}');">Contract</a> &nbsp;&nbsp;|&nbsp;&nbsp;  
		  	<a class="activelink" href="javascript:void(0);" onclick="submitFormToViewBudgetList('${contractInfo.contractId}','${contractInfo.budgetId}');">Budget</a>&nbsp;&nbsp;|&nbsp;&nbsp; 
			<c:set var="isPaymentVisibleHyperlink" value="false" />
	 		<c:if test="${contractInfo.paymentCount > 0}">
	 				<c:set var="isPaymentVisibleHyperlink" value="true"/>
	 		</c:if>
			<c:if test="${isPaymentVisibleHyperlink}">
			 	<a class="activelink" href="javascript:void(0);" onclick="submitFormToViewPaymentList('${contractInfo.contractId}','${contractInfo.invoiceId}');">
			</c:if> 
			   	Payments
			<c:if test="${isPaymentVisibleHyperlink}">
			 	</a>
			</c:if>
</span>
<!-- End : R5 Added -->			
	<div class="buttonholder">
		<c:if test="${readOnlyPageAttribute ne 'true' }">
			<input type="button" class="button" value="Save" onclick="onSaveClick('${contractInfo.invoiceId}');"/> 
			<d:content section="${sectionSubmitButton}">
			<input type="button" class="button"  id="BudgetSubmitInvoiceId" value="Submit" onclick="openOverlay();"  />
			</d:content>
		</c:if>
	</div>
	
	<%--Invoice Information Ends--%>


<%-- Container Starts --%>

<p>&nbsp;</p>

		<c:forEach var="subBudgetData" items="${BudgetAccordianData}"
			varStatus="subBudgetCounter">
			<div id="accordionTopId">
			<div class="accrodinWrapper hdng" id="accordionHeaderId"
				onclick="displayAccordion(this);if(divEmpty('invoiceSummary${subBudgetCounter.count}')){showCBGridTabsJSP('invoiceSummary','invoiceSummary${subBudgetCounter.count}','${subBudgetData.subBudgetID}','${contractInfo.invoiceId}','${contractInfo.budgetId}');}">
			<h5 class="breakAll">${subBudgetData.subBudgetName}</h5>
			<ul>
				<li>
					<label  id="subBudAmt${subBudgetCounter.count}">${subBudgetData.subBudgetAmount}
						<c:set var="subAmounttotal" value="${subAmounttotal + subBudgetData.subBudgetAmount}"></c:set>
					</label>
				</li>
			</ul>
			</div>

			<div id="accordianId" class="close">
				<div class="accContainer">
					<div id="tabs${subBudgetCounter.count}" class='accordionBorder'>
					<input type="hidden" id="hdnGridDivId" value="invoiceSummary${subBudgetCounter.count}"/>
					<input type="hidden" id="hdnGridSubBudgetId" value="${subBudgetData.subBudgetID}"/>
					<input type="hidden" id="hdnGridParentSubBudgetId" value="${contractInfo.invoiceId}"/>
						<ul class='procurementTabber'>
							<li>
								<a href='#invoiceSummary${subBudgetCounter.count}'
								 jspname='invoiceSummary'>Budget Summary</a>
							</li>
							<%-- R6 change Starts --%>
							<c:choose>
								<c:when test="${contractInfo.usesFte eq 0}">
									<%--  Start: Update in Defect-8470 --%>
									<li id="1_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
											jspname='invoicePersonnelServicesTab'>Personnel Services</a></li>
									<%--  End: Update in Defect-8470 --%>
								</c:when>
								<c:otherwise>
									<li id="1_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
											jspname='invoicePersonnelServices'>Personnel Services</a></li>
								</c:otherwise>
							</c:choose>
							<%-- R6 change Ends --%>
							<%-- <li id="1_${subBudgetCounter.count}">
								<a href='#invoiceSummary${subBudgetCounter.count}'
									 jspname='invoicePersonnelServices'>Personnel Services</a>
							</li> --%>
							<li id="2_${subBudgetCounter.count}">
								<a href='#invoiceSummary${subBudgetCounter.count}'
									 jspname='invoiceOperationSupport'>Operations &amp; Support</a>
							</li>
							<li id="3_${subBudgetCounter.count}">
								<a href='#invoiceSummary${subBudgetCounter.count}'
									 jspname='invoiceUtilities'>Utilities</a>
							</li>
							<li id="4_${subBudgetCounter.count}">
								<a href='#invoiceSummary${subBudgetCounter.count}'
									 jspname='invoiceProfessionalServices'>Professional Services</a>
							</li>
							<li id="5_${subBudgetCounter.count}">
								<a href='#invoiceSummary${subBudgetCounter.count}'
								 	 jspname='rentInvoicing'>Rent</a>
							</li>
							<li id="6_${subBudgetCounter.count}">
								<a href='#invoiceSummary${subBudgetCounter.count}'
									 jspname='contractedServicesInvoicing'>Contracted Services</a>
							</li>
							<li id="7_${subBudgetCounter.count}">
								<a href='#invoiceSummary${subBudgetCounter.count}'
									 jspname='invoiceRate'>Rate</a>
							</li>
							<li id="8_${subBudgetCounter.count}">
								<a href='#invoiceSummary${subBudgetCounter.count}'
									 jspname='milestoneInvoice'>Milestone</a>
							</li>
							<li id="9_${subBudgetCounter.count}">
								<a href='#invoiceSummary${subBudgetCounter.count}'
									 jspname='invoiceUnallocatedFunds'>Unallocated Funds</a>
							</li>
							<li id="10_${subBudgetCounter.count}">
								<a href='#invoiceSummary${subBudgetCounter.count}'
									 jspname='invoiceIndirectRate'>Indirect Rate</a>
							</li>
							<li id="11_${subBudgetCounter.count}">
							    <a href='#invoiceSummary${subBudgetCounter.count}'
									 jspname='programIncomeInvoice'>Program Income</a>
							</li>
							<%-- Start: Added in R7 for Cost-Center--%>
							<c:if test="${contractInfo.costCenterOpted eq '2'}">
								<li id="cc_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
									 jspname='invoiceServices'>Services</a></li>
							</c:if>
							<%-- End: Added in R7 for Cost-Center--%>
						</ul>
					
						<div class="clear accordionWrapper">
							<div id='invoiceSummary${subBudgetCounter.count}'></div>			
						</div>
					</div>
				</div>
			</div>
			</div>
		</c:forEach>

		<div id="accordionTopId1">
			<div class="accrodinWrapper hdng" id="accordionHeaderId1" onclick="displayAccordion(this);">
				<h5>Documents</h5>
				<ul>
					<li><label>&nbsp;</label>
					</li>
				</ul>
			</div>
		<div id="accordianId1" class="close">
			<div class="accContainer" id="documentWrapper"></div>
			</div>
		</div>
		<div id="accordionTopId2">
			<div class="accrodinWrapper hdng" id="accordionHeaderId2" onclick="displayAccordion(this);if(divEmpty('advanceWrapper')){showCBGridTabsJSP('invoiceAdvances', 'advanceWrapper', '','${contractInfo.invoiceId}','${contractInfo.budgetId}');}">
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
			<div class="accrodinWrapper hdng" id="accordionHeaderId3" onclick="displayAccordion(this);if(divEmpty('assignmentWrapper')){showCBGridTabsJSP('invoiceAssignments', 'assignmentWrapper', '','${contractInfo.invoiceId}','${contractInfo.budgetId}');}">
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
		</div>
		<!-- R6: Returned Payment change Starts -->
		<c:if test="${contractInfo.noOfReturnedPayments > 0}">
		<div id="accordionTopId4">
					<div class="accrodinWrapper hdng" id="accordionHeaderId4"
						onclick="displayAccordion(this); if(divEmpty('returnedPaymentWrapper')){showCBGridTabsJSP('returnedPaymentReadOnly', 'returnedPaymentWrapper', '','');}">
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
				<!-- R6: Returned Payment change Ends -->
	</form:form>
	<div id="tempDocument" style="display:none">
		<jsp:include page="/WEB-INF/r2/jsp/tasks/document.jsp" /></div>
		<p class='clear'>&nbsp;</p>	
			<c:set var="entityType"><%=HHSConstants.AUDIT_INVOICES%></c:set>
	<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_INVOICE_REVIEW%></c:set>
	<%--Added attribute textAreaSize for Defect-8509 --%>
	<div class='gridFormField'>
		<task:taskContent
		workFlowId=""
		taskType="taskContractBudgetReview"
		entityType="${entityType}"
		entityTypeForAgency="${entityTypeForAgency}"
		level="footer"
		textAreaSize="3000">
		</task:taskContent>
	</div> 
</d:content>

<%--  Overlay popup starts --%>
<div class="overlay"></div>
	  <div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>
			<div class="alert-box-contact">
				<div id="contactDiv"></div>
			</div>
<div class="alert-box-submit-invoice" id="overlayDivId"></div>
<input type='hidden' value='${subAmounttotal}' id='hiddenSubAmountTotal' />


<div class="alert-box-add-assign" id="overlayDivId1"></div>
</c:when>
<c:otherwise>
<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
</c:otherwise>
</c:choose>
