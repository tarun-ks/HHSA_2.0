/**
 * ===================================================
 * This file handle the events on Payment List Screen.
 *
 ****************** Start : Added in R5 **************
 *
 * ===================================================
 * */
$(document).ready(function() {
	$(".tabularWrapper select").change(function() {
		var paymentAction=$(this).val();
		var contractId=$(this).attr("contractId");
		var budgetId=$(this).attr("budgetId");
		var invoiceId=$(this).attr("invoiceId");
		if (paymentAction.toLowerCase() == "view contract") {
			submitFormToViewContractList(contractId);
		}
		else if (paymentAction.toLowerCase() == "view budget") {
			submitFormToViewBudgetList(contractId, budgetId);
		}
		else if (paymentAction.toLowerCase() == "view invoice") {
			submitFormToViewInvoiceList(contractId, budgetId, invoiceId);
		}
	});
});
/**
 * Function : form submit on select of View Contract
 * */
function submitFormToViewContractList(contractId){
	document.paymentform.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=contractListAction&contractId="+contractId;
	document.paymentform.submit();
}
/**
 * Function : form submit on select of View Budget
 * */
function submitFormToViewBudgetList(contractId, budgetId){
	document.paymentform.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=budgetListAction&contractId="+contractId+"&budgetId="+budgetId;
	document.paymentform.submit();
}
/**
 * Function : form submit on select of View Invoice
 * */
function submitFormToViewInvoiceList(contractId, budgetId, invoiceId){
	document.paymentform.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=invoiceListAction&contractId="+contractId+"&budgetId="+budgetId+"&invoiceId="+invoiceId;
	document.paymentform.submit();
}
/**
 * End : Added in R5
* Javascript for filter popup
 */
function setVisibility(id, visibility) {
	callBackInWindow("closePopUp");
    if ($("#" + id).is(":visible")) {
          document.paymentform.reset();
    }
	$("#" + id).toggle();
	disableProgramDropDown();
}
/**
 *  This method fetched Amended Contract List
 * */
function fetchAmendedContractList() {
	document.paymentform.action = document.paymentform.action
			+ '&next_action=filterPayment';
	document.paymentform.submit();
}
/**
 *  Set the filter to default values
 **/
function settoDefaultFilters() {
	$('input:text').val('');
	$('select').find('option:first').attr('selected', 'selected');
	$('input:checkbox').attr('checked', true);
	$('.paymentRejected').attr('checked', false);
	$("span.error").empty();
	disableProgramDropDown();
}

/**
 *  This will execute when any Column header on grid is clicked for sorting
 * */
function sort(columnName) {
	document.paymentform.reset();
	$("#paymentform").attr(
			"action",
			$("#paymentform").attr("action")
					+ "&next_action=sortPayment&sortGridName=paymentSort"
					+ sortConfig(columnName));
	document.paymentform.submit();
}

/**
 *  This will execute when Previous,Next.. is clicked for pagination
 *  */
function paging(pageNumber) {
	document.paymentform.reset();
	$("#paymentform")
			.attr(
					"action",
					$("#paymentform").attr("action")
							+ "&next_action=fetchActivePayments&nextPage="
							+ pageNumber);
	document.paymentform.submit();
}
/**
 *  This method opens Payment Details
 *  */
function openPaymentDetails(contractId, budgetId,invoiceId,paymentId,budgetAdvanceId, selectIndex) {
	var url;
	if(invoiceId !="null")
		{
		url = contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_payment_detail&_nfls=false" +
			"&contractId=" + contractId	+ "&budgetId=" + budgetId+ "&invoiceId=" + invoiceId+ "&paymentId=" + paymentId;
		}
	else 
		{
		url = contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_advance_payment_detail&_nfls=false" +
		"&contractId=" + contractId	+ "&budgetId=" + budgetId+ "&paymentId=" + paymentId+ "&budgetAdvanceId=" + budgetAdvanceId;
		}
	  location.href= url;
}

// On load of the document
var suggestionVal = "";
$(document)
		.ready(
				function() {
					disableProgramDropDown();
					
					$("#agency").change(function() {
						agency = $(this).prop("selectedIndex");
						if (agency == 0) {
							$("#programName").val("");
							$("#programName").prop('disabled', true);
						} else {
							getProgramNameList();
						}
					});
					 $('#programNameListScreen').val($("#lsProgramNameIdListScreen").val());
					$(".tablePaymentValue").each(function(e) {
						$(this).validateCurrencyOnLoad();
					});
					
					
					//disableIdSetToDefault();
					var orgType = $("#orgType").val();
					if (orgType == "agency_org" || orgType == "city_org") {
						typeHeadSearch($('#paymentProvider'), $("#hiddengetProviderListResourceUrl").val(),null,"typeHeadCallBackProvider",null);
					}
					typeHeadSearch($('#paymentCtId'), $(
							"#hiddenPaymentCtResourceUrl").val()
							+ "&epinQueryId=getPaymentCtId",null,"typeHeadCallBackCtId",null);
					$('#paymentValueFrom').autoNumeric({
						vMax : '99999999999.99'
					});
					
					//release 3.3.0 enhancement id	6248
					$(".paymentListProviderDiv tr>th:nth-child(6)").addClass("alignRht");
					if (orgType == "city_org") {
						$(".paymentListCityDiv tr>th:nth-child(6)").addClass("alignRht");
					}
					else
					{
						$(".paymentListCityDiv tr>th:nth-child(5)").addClass("alignRht");
					}
					$('#paymentValueTo').autoNumeric({
						vMax : '99999999999.99'
					});
					$(".tableContractValue").each(function(e) {
						$(this).autoNumeric('init', {vMax: '9999999999999999',vMin:'-9999999999999999.99'});
					});
				});
/**
 *  This method submits List Payment
 *  */
function submitListPayment()
{	var containtsNonRequiredCharacter = false;
	$('#paymentform input:text').each(function() {
		if(removeNonRequiredCharacter(this)){
			containtsNonRequiredCharacter = true;
		}
		
	});
	if(!containtsNonRequiredCharacter)
	{
	$("#dateCheck").empty();
	var startDate = new Date($("#dateLastUpdateFrom").val());     
	var endDate = new Date($("#dateLastUpdateTo").val());
	var startDate2 = new Date($("#dateDisbursedFrom").val());     
	var endDate2 = new Date($("#dateDisbursedTo").val());
	if(!checkStartEndDatePlanned(startDate,endDate) )
		{
		$("#dateCheck").html('"Date of Last Update to:" field less than "Date of Last Update From:"');
		}
	else if(checkStartEndDatePlanned(startDate2,endDate2)){
		if(checkForDate()){
	document.paymentform.action = document.paymentform.action
			+ '&next_action=filterPayment';
	document.paymentform.submit();
	pageGreyOut();
		}
	}
	else{
		$("#dateCheck").html('"Date Disbursed to:" field less than "Date Disbursed From:"');
	}
		}
}
/**
 * This method fetches program name list depending upon agency selected by user
 * */
function getProgramNameList() {
	var url = $("#getProgramListForAgency").val() + "&agencyId="
			+ $("#agency").val();
	var jqxhr = $.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(data) {
			if($("#agency").prop("selectedIndex")!=0)
			{
			$("#programName").html(data);
			$("#programName").prop('disabled', false);
			}
		},
		error : function(data, textStatus, errorThrown) {
		}
	});
}
/**
 * This method disables Program DropDown
 * */
function disableProgramDropDown() {
	if(($("#orgType").val()) != 'agency_org' && $("#agency").prop("selectedIndex")==0)
	{
	$("#programName").prop('disabled', true);
	}
	}
/**
 *  This method types Head Call Back Provider
 *  */
function typeHeadCallBackProvider() {
	commonTypeHeadCallBack($('#paymentProvider').val());
	}
/**
 * This method types Head Call Backct Id
 * */
function typeHeadCallBackCtId() {
	commonTypeHeadCallBack($('#paymentCtId').val());
	}

/**
 * Added method for Enhancement  id 6356   release 3.4.0
 * */
//Emergency Build 4.0.1 defect 8358
function launchBudget(budgetId,contractId,fiscalYearId,budgetType,ctId)
{
	document.forms[0].reset();
	$("#paymentform").attr(
			"action",
			$("#navigateToContractBudgetURL").val() + "&budgetId="
					+ budgetId + "&contractId="+contractId + "&ctId="+ctId + "&fiscalYearId="+fiscalYearId + "&budgetType="+budgetType+"&loadModificationFirst=false&removeSessionValue=true");
	document.paymentform.submit();
}
/**
 * Added method for Enhancement  id 6356   release 3.4.0
 * */
function viewInvoice(invoiceId){
	document.forms[0].reset();
	$("#paymentform").attr(
			"action",
			$("#hiddenViewInvoiceUrl").val() + "&invoiceId="
					+ invoiceId);
	document.paymentform.submit();
	}