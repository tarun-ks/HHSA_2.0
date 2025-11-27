/**
 * ===============================================
 * This file handle the event of Invoice Screen.
 * 
 * ************Start : Added in R5****************
 * 
 * ===============================================
 **/
$(document).ready(function() {
	$(".tabularWrapper select").change(function() {
		var contractAction = $(this).val();
		var contractId = $(this).attr('contractId');
		var invoiceId = $(this).attr('invoiceId');
		var budgetId = $(this).attr('budgetId');
		if(contractAction.toLowerCase() == 'view invoice')
		{
			viewInvoice(invoiceId);
		}
		else if(contractAction.toLowerCase() == 'withdraw')
		{
			withdrawDocument(invoiceId);
		}
		else if(contractAction.toLowerCase() == 'delete')
		{
			deleteDocument(invoiceId);
		}
		else if(contractAction.toLowerCase() == 'view contract')
		{
			submitFormToViewContractList(contractId);
		}
		else if(contractAction.toLowerCase() == 'view budget')
		{
			submitFormToViewBudgetList(contractId, budgetId);
		}
		else if(contractAction.toLowerCase() == 'view payments')
		{
			submitFormToViewPaymentList(contractId, budgetId, invoiceId);
		}
	});
});
/**
 * Function : form submit on select of View Contract
 * */
function submitFormToViewContractList(contractId){
	document.invoiceFilterForm.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=contractListAction&contractId="+contractId;
	document.invoiceFilterForm.submit();
}
/**
 * Function : form submit on select of View Budget
 * */
function submitFormToViewBudgetList(contractId, budgetId){
	document.invoiceFilterForm.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=budgetListAction&contractId="+contractId+"&budgetId="+budgetId;
	document.invoiceFilterForm.submit();
}
/**
 * Function : form submit on select of View Payment
 * */
function submitFormToViewPaymentList(contractId, budgetId, invoiceId){
	document.invoiceFilterForm.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=paymentListAction&contractId="+contractId+"&budgetId="+budgetId+"&invoiceId="+invoiceId;
	document.invoiceFilterForm.submit();
}
/*End : Added in R5*/

/**
 * Javascript for filter popup
 * */
function setVisibility(id, visibility) {
	callBackInWindow("closePopUp");
	if ($("#" + id).is(":visible")) {
		document.invoiceFilterForm.reset();
	}
	$("#" + id).toggle();
	disableProgramDropDown();
}

/**
 * This is the functionality to View an Invoice by the click of the hyperlink
 * */
function viewInvoice(invoiceId){
	document.forms[0].reset();
	$("#invoiceFilterForm").attr(
			"action",
			$("#hiddenViewInvoiceUrl").val() + "&invoiceId="
					+ invoiceId);
	document.invoiceFilterForm.submit();
	}

/**
 * This Function is to delete invoice by providers that are returned from invoiceList screen.
*/
function deleteDocument(invoiceId){
	pageGreyOut();
	var v_parameter = '&next_action=deleteInvoice'+'&invoiceId=' + invoiceId;
	var urlAppender = $("#hiddenDeleteInvoiceUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayContent").html(e);
			$(".overlay").launchOverlayNoClose($(".alert-box-link-to-vault"),
					"850px", null,"onLoad");
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
				refresh();
			});
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 * Function to withdraw invoices by providers that are returned by agency from invoiceList screen.
 * */
function withdrawDocument(invoiceId){
	pageGreyOut();
	var v_parameter = '&next_action=withdrawInvoice'+'&invoiceId=' + invoiceId;
	var urlAppender = $("#hiddenWithdrawInvoiceUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayContent").html(e);
			$(".overlay").launchOverlayNoClose($(".alert-box-link-to-vault"),
					"850px", null,"onLoad");
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
			});
			//JQuery validations.
			$('#textEnter').focus(function(){
				if(($(this).val()).toString()=="Enter comments"){
					$(this).val('');
				}
			});
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/**
 *  Set the filter to default values
 *  */
function settoDefaultFilters() {
	$('input:text').val('');
	$('select').find('option:first').attr('selected', 'selected');
	$('input:checkbox').attr('checked', true);
	$("span.error").empty();
	disableProgramDropDown();
}
/**
 *  This will execute when Previous,Next.. is clicked for pagination
 * */
function paging(pageNumber) {
	document.invoiceFilterForm.reset();
	$("#invoiceFilterForm").attr(
			"action",
			$("#invoiceFilterForm").attr("action")
					+ "&next_action=fetchNextInvoices&nextPage=" + pageNumber);
	document.invoiceFilterForm.submit();
}

/**
 *  Method wil be executed once any column header is clicked on InvoiceList.jsp
 *	page for sorting
 **/
function sort(columnName) {
	document.invoiceFilterForm.reset();
	$("#invoiceFilterForm")
			.attr(
					"action",
					$("#invoiceFilterForm").attr("action")
							+ "&next_action=sortInvoiceList&sortGridName=invoiceListMap"
							+ sortConfig(columnName));
	document.invoiceFilterForm.submit();
}
/**
 * This Function is called to fetch Amended Contract List.
 * */
function fetchAmendedContractList() {
	document.invoiceFilterForm.action = document.invoiceFilterForm.action
			+ '&next_action=filterInvoice';
	document.invoiceFilterForm.submit();
	document.invoiceFilterForm.submit();
}
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
					var orgType = $("#orgType").val();
					if (orgType == "agency_org" || orgType == "city_org") {
						typeHeadSearch($('#invoiceProvider'), $("#hiddengetProviderListResourceUrl").val(),null,"typeHeadCallBackProvider",null);
					}
					typeHeadSearch($('#invoiceCtId'),$("#hiddenInvoiceCtResourceUrl").val()+
					"&epinQueryId=getInvoiceCtId",null,"typeHeadCallBackCtId",null);
					$('#invoiceValueFrom').autoNumeric({
						vMax : '99999999999.99'
					});
					$('#invoiceValueTo').autoNumeric({
						vMax : '99999999999.99'
					});
					$(".tableInvoiceValue").each(function(e) {
						$(this).autoNumeric('init', {vMax: '9999999999999999',vMin:'-9999999999999999.99'});
					});
					//made changes for defect id 6248 release 3.3.0
					if (orgType == "city_org") {
						$(".invoiceListDiv tr>th:nth-child(7)").addClass("alignRht");
					}
					else
					{
					$(".invoiceListDiv tr>th:nth-child(6)").addClass("alignRht");
					}
					// perform the validation like required field check, email
					// check, maxlength check etc .on the funder screen.
				});
/**
 *  This  method submits the filter form
 **/
function submitListInvoice()
{
	var containtsNonRequiredCharacter = false;
	$('#invoiceFilterForm input:text').each(function() {
		if(removeNonRequiredCharacter(this)){
			containtsNonRequiredCharacter = true;
		}
		
	});
	if(!containtsNonRequiredCharacter)
	{
	$("#dateCheck, #dateCheck2").empty();
	var startDate = new Date($("#dateSubmittedFrom").val());     
	var endDate = new Date($("#dateSubmittedTo").val());
	var startDate2 = new Date($("#dateApprovedFrom").val());     
	var endDate2 = new Date($("#dateApprovedTo").val());
	if(checkForDate())
	{
	if(checkStartEndDatePlanned(startDate,endDate) &&
			checkStartEndDatePlanned(startDate2,endDate2))
	{
	document.invoiceFilterForm.action = document.invoiceFilterForm.action
			+ '&next_action=filterInvoice';
	document.invoiceFilterForm.submit();
	pageGreyOut();
	}
	else{
		$("#dateCheck, #dateCheck2").html("! End Date can not be less than Start Date.");
	}
}
	}
}
/**
 *  This method displays and populate the overlay
 *  */
function fillAndShowOverlay(contractType,invoiceId) {
	var jspName = getJspName(contractType);
	if(jspName=="")
		return;
	var v_parameter = "jspName=" + jspName+"&invoiceId="+invoiceId;
	var urlAppender = $("#hiddenInvoiceTypeOverlayPageUrl").val();
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayDivId").html(e);
			$(".overlay").launchOverlayNoClose($(".alert-box-delete-invoice"),
					"850px", null, "onReady");
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
			});
			 removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			 removePageGreyOut();
		}
	});
}
/**
 *  this method returns the JSP name in accordance to the option selected
 *  */
function getJspName(invoiceActionType) {
	var returnJspName = "";
	switch (invoiceActionType) {
	case "View Invoice":
		returnJspName = "viewInvoice";
		break;
	case "Delete Invoice":
		returnJspName = "deleteInvoice";
		break;
	case "Withdraw Invoice":
		returnJspName = "withdrawInvoice";
		break;	
	}
	return returnJspName;
}
/**
 * This function fetches program name list depending upon agency selected by user
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
 *  This method disables the program drop down
 *  */
function disableProgramDropDown() {
	if(($("#orgType").val()) != 'agency_org' && $("#agency").prop("selectedIndex")==0)
	{
	$("#programName").prop('disabled', true);
	}
	}
/**
 *  This method calls commonTypeHeadCallBack passing invoiceProvider as a parameter
*/
function typeHeadCallBackProvider() {
	commonTypeHeadCallBack($('#invoiceProvider').val());
	}
/**
 * This method calls commonTypeHeadCallBack passing invoiceCtId as a parameter
*/
function typeHeadCallBackCtId() {
	commonTypeHeadCallBack($('#invoiceCtId').val());
	}
/**
 * Added method for Enhancement  id 6461  release 3.4.0
*/
//Emergency Build 4.0.1 defect 8358
function launchBudget(budgetId,contractId,fiscalYearId,budgetType,ctId)
{
	document.forms[0].reset();
	$("#invoiceFilterForm").attr(
			"action",
			$("#navigateToContractBudgetURL").val() + "&budgetId="
					+ budgetId + "&contractId="+contractId + "&ctId="+ctId + "&fiscalYearId="+fiscalYearId + "&budgetType="+budgetType+"&loadModificationFirst=false&removeSessionValue=true");
	document.invoiceFilterForm.submit();
}