/**
 * =========================================================================
 ***  Contains method that will handles Budget List.
 * 
 ***  This will execute when any Column header on grid is clicked for sorting.
 * ==========================================================================
 **/
var isvalid = false;
var suggestionVal = "";
$(document).ready(function() {
	disableProgramDropDown();
	
	$("#authenticate").hide();
	$("#btnYesDeleteBudgetUpdate").attr("disabled","disabled");		
	
	
	$("#agency").change(function() {
		agency = $(this).prop("selectedIndex");
		if (agency == 0) {
			$("#programName").val("");
			$("#programName").prop('disabled', true);
		} else {
			getProgramNameList();
		}
	});
	$('#budgetValueFrom').autoNumeric('init', {vMax: '9999999999999999.99',vMin:'-9999999999999999.99'});
	$('#budgetValueTo').autoNumeric('init', {vMax: '9999999999999999.99',vMin:'-9999999999999999.99'});
	$(".tableBudgetValue").each(function(e) {
		$(this).autoNumeric('init', {vMax: '9999999999999999',vMin:'-9999999999999999.99'});
	});
	
	if($("#aoProcValue").html() != null && typeof $("#aoProcValue").val() != 'undefined'){
		$("#aoProcValue").jqGridCurrency();
	}
	
	var orgType = $("#orgType").val();
	if (orgType == "agency_org" || orgType == "city_org") {
		typeHeadSearch($('#awardEpin'), $("#hiddengetEpinListResourceUrl").val()
				+ "&epinQueryId=fetchBudgetEpinList",null,"typeHeadCallBackAward",null);
		typeHeadSearch($('#providerName'), $("#hiddengetProviderListResourceUrl").val(),null,"typeHeadCallBackProvider",null);
	}
	typeHeadSearch($('#ctId'), $("#hiddengetContractNoListResourceUrl").val()
			+ "&budgetNoQueryId=fetchBudgetNoList",null,"typeHeadCallBackCtId",null);
	
	//release 3.3.0 enhancement id	6248
	if (orgType == "city_org") {
	$(".budgetListDiv tr>th:nth-child(6)").addClass("alignRht nowrap");
	}
	else
	{
		$(".budgetListDiv tr>th:nth-child(5)").addClass("alignRht nowrap");	
	}
	
	$(".tabularWrapper select").change(function() {
		var budgetAction=$(this).val();
		var budgetId=$(this).attr("budgetId");
		var contractId=$(this).attr("contractId");
		var budgetType=$(this).attr("budgetType");
		var fiscalYearId=$(this).attr("fiscalYear");
		var agencyName=$(this).attr("agencyName");
		var programId=$(this).attr("programId");
		var ctId=$(this).attr("ctId");
		/** Start QC9149  R 7.7.0 */
		var cntNegativeAmend=$(this).attr("negativeAmendCnt");
        /** End QC9149  R 7.7.0 */
		/** Start QC9490  R 8.4.0 */
		var deleteBudgetUpdateFlag=$(this).attr("deleteBudgetUpdateFlag");
        /** End QC9490  R 8.4.0  */
  
		if(budgetAction=='View Budget' && (budgetType=='Contract Budget'||budgetType=='Budget Amendment'|| budgetType=='Budget Modification'||budgetType=='Budget Update')){
			launchBudget(budgetId,contractId,fiscalYearId,budgetType,ctId);
		}
		//Start : R5 Added 
		else if (budgetAction.toLowerCase() == "view contract") {
			submitFormToViewContractList(contractId);
		}
		else if (budgetAction.toLowerCase() == "view invoices") {
			submitFormToViewInvoiceList(budgetId);
		}
		else if (budgetAction.toLowerCase() == "view payments") {
			submitFormToViewPaymentList(contractId, budgetId);
		}
		else if (budgetAction.toLowerCase() == "view amendment") {
			//Made changes for Emergency Build 4.0.0.3 defect 8397 and 8313
			submitFormToViewAmendmentList(contractId, contractId);
		}
		//End : R5 Added

		//[Start]added in R7.7.0 QC9149
		else if( (budgetAction.toLowerCase() == "initiate advance" || budgetAction.toLowerCase() == "request advance") 
		          && parseInt(cntNegativeAmend) > 0 )
		{
		    launchBudgetNegativeAmendment(budgetId,contractId,fiscalYearId,budgetType,ctId,cntNegativeAmend);
		}
		//[End] added in R7.7.0 QC9149
		else if(budgetAction=='Submit Invoice')
			{
			  launchInvoice(budgetId,contractId,fiscalYearId,agencyName,programId);
			}
		else if(budgetAction=='Update Budget Template')
		{
			launchUpdateBudgetTemplateOverlay(budgetAction,budgetId,contractId,fiscalYearId,budgetType,this);
		}
		// Start : Added for R7
		else if(budgetAction=='Cancel and Merge')
		{
			launchCancelMergeOverlay(budgetAction,budgetId,contractId,fiscalYearId,budgetType,this);
		}
		else if(budgetAction=='Update Services')
		{
			launchUpdateServicesOverlay(budgetAction,budgetId,contractId,fiscalYearId,budgetType,this);
		}
		// End : Added for R7
		else if(budgetAction!='View Budget')
		{
			launchBudgetListOverlay(budgetAction,budgetId,contractId,fiscalYearId,budgetType,this);
		}

		$(this).prop('selectedIndex',0);
		$(this).blur();
		pageGreyOut();
	});	
	$("a.exit-panel").click(function(){	
			$(".overlay").closeOverlay();				
		$('.budgetRequestAdvance').find('option:first').attr('selected', 'selected');
	});	
	if($("#successMsgBudgetList").html()!="")
		$("#successMsgBudgetList").show();
// Filter Validation
	
});
/**
*  Start : Added in R5 
*  Function : form submit on select of View Contract
**/
function submitFormToViewContractList(contractId){
	document.financebudgetform.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=contractListAction&contractId="+contractId;
	document.financebudgetform.submit();
}
/**
 * Function : form submit on select of View Invoices
 **/
function submitFormToViewInvoiceList(budgetId){
	document.financebudgetform.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=invoiceListAction&budgetId="+budgetId;
	document.financebudgetform.submit();
}
/**
 * Function : form submit on select of View Payment
 **/
function submitFormToViewPaymentList(contractId, budgetId){
	document.financebudgetform.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=paymentListAction&contractId="+contractId+"&budgetId="+budgetId;
	document.financebudgetform.submit();
}
/**
 * Function : form submit on select of View Amendment
 **/
function submitFormToViewAmendmentList(contractId, amendcontractid){
	//Made changes for Emergency Build 4.0.0.3 defect 8397 and 8313
	document.financebudgetform.action = $("#hiddenNavigateListScreenUrl").val()+"&next_action=amendment&listAction=contractListAction&contractId="+contractId+"&amendcontractid="+amendcontractid;
	document.financebudgetform.submit();
}
//End : Added in R5
/**
 * This method sumbit list of budget
 * It will check the date validations, if correct then submit list.
 * This method update in R7.
 */
function submitListBudget()
{
	//added in R7
	var modifChecked=$('#chkApprovedModif').is(':checked');
	//r7 end
	var containtsNonRequiredCharacter = false;
	$('#financebudgetform input:text').each(function() {
		if(removeNonRequiredCharacter(this)){
			containtsNonRequiredCharacter = true;
		}
		
	});
	if(!containtsNonRequiredCharacter)
	{
	$("#dateCheck").empty();
	var startDate = new Date($("#dateLastUpdateFrom").val());     
	var endDate = new Date($("#dateLastUpdateTo").val());
	if(checkForDate())
	{
	if(checkStartEndDatePlanned(startDate,endDate))
	{
			document.financebudgetform.action = document.financebudgetform.action
			+ '&next_action=filterBudget'+'&approvedModifChecked='+modifChecked;
			document.financebudgetform.submit();
			pageGreyOut();
	}
	else
	{
		$("#dateCheck").html("! End Date can not be less than Start Date.");
	}
	}
	}
}
/**
 * This method is called to launch budget
 **/
function launchBudget(budgetId,contractId,fiscalYearId,budgetType,ctId)
{
	document.forms[0].reset();
	$("#financebudgetform").attr(
			"action",
			$("#navigateToContractBudgetURL").val() + "&budgetId="
					+ budgetId + "&contractId="+contractId + "&ctId="+ctId + "&fiscalYearId="+fiscalYearId + "&budgetType="+budgetType+"&loadModificationFirst=false");
	document.financebudgetform.submit();
}

/** Start QC9149  R 7.7.0 */
function launchBudgetNegativeAmendment(budgetId,contractId,fiscalYearId,budgetType,ctId, negativeAmendCnt)
{
    document.financebudgetform.action = document.financebudgetform.action 
                                    + "&listAction=budgetListAction&contractId="+contractId+"&budgetId="+budgetId + "&negativeAmendCnt="+negativeAmendCnt; 
			document.financebudgetform.submit();
			pageGreyOut();
}
/** End QC9149  R 7.7.0 */


/**
 * This method is called to launch invoice
 **/
function launchInvoice(budgetId,contractId,fiscalYearId,agencyName,programId)
{
	document.forms[0].reset();
	$("#financebudgetform").attr(
			"action",
			$("#navigateToContractInvoiceURL").val() + "&budgetId="
					+ budgetId + "&contractId="+contractId + "&fiscalYearID="+fiscalYearId + "&agencyId="+agencyName+"&programId="+programId);
	document.financebudgetform.submit();
}

/**
 *  This method is called to sort Budget List.
 **/
function sort(columnName) {
	document.forms[0].reset();
	$("#financebudgetform").attr(
			"action",
			$("#financebudgetform").attr("action")
					+ "&next_action=sortBudgetList&sortGridName=budgetListMap"
					+ sortConfig(columnName));
	document.financebudgetform.submit();
}

/**
 *  This will execute when Previous,Next.. is clicked for pagination
 **/
function paging(pageNumber) {
	document.forms[0].reset();
	$("#financebudgetform").attr(
			"action",
			$("#financebudgetform").attr("action")
					+ "&next_action=fetchActiveBudgets&nextPage=" + pageNumber);
	document.financebudgetform.submit();
}
/**
 * This method is called to set Default To Filters.
 **/
function setDefaultToFilters() {
	$('input:text').val('');
	$('select').find('option:first').attr('selected', 'selected');
	$('input:checkbox').attr('checked', 'checked');
	//added in R7
	$('#chkApprovedModif').attr('checked', false);
	//R7 end
	$("input[id^=unSelectedCheckBox]").each(function() {
		$(this).attr("checked", false);
	});
	$("span.error").empty();
	disableProgramDropDown();
}
/**
 * This method is called to clear And Close OverLay.
 **/
function clearAndCloseOverLay() {
	$("#overlayContent").html("");
	$(".overlay").closeOverlay();
}

/**
 *  Function to launch overlay inside BudgetList list screen.
 **/
function launchBudgetListOverlay(budgetAction, budgetId, contractId, fiscalYearId, budgetType, selectElement) {
	pageGreyOut();
	var v_parameter = '&next_action=' + budgetAction + '&budgetId=' + budgetId+ '&ContractId=' + contractId + '&fiscalYearId=' + fiscalYearId
	+ '&budgetType=' + budgetType;
	var urlAppender = $("#hiddenSelectOverayContentUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayContent").html(e);
			$(".overlay").launchOverlay($(".alert-box-link-to-vault"),
					$(".exit-panel"), null, null);
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/**
 * This method is called to modify Budget Button.
 **/
function modifyBudgetButton() {
	pageGreyOut();
	var url = $("#createButtonLinkURL").val();
	jQuery.ajax({
		type : "POST",
		url : url,
		success : function(e) {
			var data = $(e).find("#errorMessageDiv").html();
			if (data.length==0) {
// Add code to launch the next screen
				$("#overlay").closeOverlay();
				$("#confirmBudgetModificationForm").attr("action",
						$("#confirmBudgetModificationForm").attr("action") + "&loadModificationFirst=true");
				$("#confirmBudgetModificationForm").submit();
			} else {
				$(".failedShow").show();
				$("#errorMessageDiv").html(data);
			}
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/* [Start] R7.4.0 QC9008 Add abilities to delete budget update task  */
/**
 * This method is called to delete Budget Update task.
 **/
function deleteBudgetUpdate() {
	pageGreyOut();
	var url = $("#hdnConfirmDeleteBudgetUpdate").val();
	var v_parameter = '&contractId=' + $("#contractId").val()+ '&budgetType=' + $("#budgetType").val()  ;

	jQuery.ajax({
		type : "POST",
		url : url,
		data : v_parameter,
		success : function(e) {
			var data = $(e).find("#errorMessageDiv").html();
			removePageGreyOut();
			$(".overlay").closeOverlay();
			window.location.href = $("#duplicateRender").val();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/* [End] R7.4.0 QC9008 Add abilities to delete budget update task  */

/**
 * This method is called to check if start and end date is not empty and endDate is less then startDate.
 **/
function checkStartEndDatePlanned(startDate, endDate){
	   if(startDate != '' && endDate != '' && (startDate > endDate))
		 return false;
	   else
		 return true;
}

/**
 * This method is called to enable Submit Button.
 **/
function enableSubmitButton(){
	if ($("#finishtaskchild option:selected").index() == 0) {
		$("#finish").attr("disabled", "disabled");
			}
	else
		{
		$("#finish").removeAttr("disabled", "disabled");
		}
}

/**
 * This funtion fetches program name list depending upon agency selected by user
 **/
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
 * This method is called to disable Program Drop Down.
 **/
function disableProgramDropDown() {
	if(($("#orgType").val()) != 'agency_org' && $("#agency").prop("selectedIndex")==0)
	{
	$("#programName").prop('disabled', true);
	}
	}
/**
 * This call back function exceuted for providerName
 */
function typeHeadCallBackProvider() {
	commonTypeHeadCallBack($('#providerName').val());
	}

/**
 * This call back function exceuted for argument awardEpin
 */
function typeHeadCallBackAward() {
	commonTypeHeadCallBack($('#awardEpin').val());
	}

/**
 * This call back function executed for argument contractId: ctId
 */
function typeHeadCallBackCtId() {
	commonTypeHeadCallBack($('#ctId').val());
	}

/**
*  R4 Update Budget Template
*	This method is called to launch Update Budget Template Overlay.
**/
function launchUpdateBudgetTemplateOverlay(budgetAction, budgetId, contractId, fiscalYearId, budgetType, selectElement) {
	pageGreyOut();
	var v_parameter = '&next_action=' + budgetAction + '&budgetId=' + budgetId+ '&contractId=' + contractId + '&fiscalYearId=' + fiscalYearId
	+ '&budgetType=' + budgetType;
	var urlAppender = $("#BudgetCustomizedTabOverlay").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayContent").html(e);
			$(".overlay").launchOverlay($(".alert-box-link-to-vault"),
					$(".exit-panel"), "540px");
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/**
*  Added in R7 
*	This method is called to launch Cancel merge overlay.
**/
function launchCancelMergeOverlay(budgetAction, budgetId, contractId, fiscalYearId, budgetType, selectElement) {
	pageGreyOut();
	var v_parameter = '&next_action=' + budgetAction + '&budgetId=' + budgetId+ '&contractId=' + contractId + '&fiscalYearId=' + fiscalYearId
	+ '&budgetType=' + budgetType;
	var urlAppender = $("#CancelAndMergeOverlayUrl").val();
	
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayContent").html(e);
			$(".overlay").launchOverlay($(".alert-box-link-to-vault"),
					$(".exit-panel"), "540px");
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

	/* [Start] R7.4.0 QC9008 Add abilities to delete budget update task  */
	function hideUnhideUsername(obj){
		if($(obj).attr('checked') || $(obj).attr('checked')=='checked'){
			$("#authenticate").show();
			$("#btnYesDeleteBudgetUpdate").removeAttr("disabled");
			$("#btnYesDeleteBudgetUpdate").addClass("redbtutton");
		}else{
			$("#authenticate").hide();
			$("#txtDeleteBudgetUpdateUserName").attr('value','');
			$("#txtDeleteBudgetUpdatePassword").attr('value','');
			$("#txtDeleteBudgetUpdateUserName").parent().next().html('');
			$("#txtDeleteBudgetUpdatePassword").parent().next().html('');
			$("#btnYesDeleteBudgetUpdate").attr("disabled","disabled");
			$("#btnYesDeleteBudgetUpdate").removeClass("redbtutton");
		}
	}


	/**
	 * This method is called to delete Budget Update task.
	 **/
	function deleteBudgetUpdate() {
		pageGreyOut();
		var url = $("#hdnConfirmDeleteBudgetUpdate").val();
		$("#delBudgetUpdateComment").val($("#deleteBudgetUpdateComment").val());

/*		var v_parameter = '&contractId=' + $("#contractId").val()+ '&budgetType=' + $("#budgetType").val()  ;  */
	    var v_parameter = "&" + $("#confirmDeleteBudgetUpdateForm").serialize();
		jQuery.ajax({
			type : "POST",
			url : url,
			data : v_parameter,
			success : function(e) {
				var data = $(e).find("#errorMessageDiv").html();
				removePageGreyOut();
				$(".overlay").closeOverlay();
				window.location.href = $("#duplicateRender").val();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
/* [End] R7.4.0 QC9008 Add abilities to delete budget update task  */

/**
*  Added in R7 
*	This method is called to launch Update Services S504 overlay.
**/
function launchUpdateServicesOverlay(budgetAction, budgetId, contractId, fiscalYearId, budgetType, selectElement) {
	pageGreyOut();
	var v_parameter = '&budgetId=' + budgetId+ '&contractId=' + contractId + '&fiscalYearId=' + fiscalYearId
	+ '&budgetType=' + budgetType;
	var urlAppender = $("#updateServicesOverlayUrl").val();
	
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayContent").html(e);
			$(".overlay").launchOverlay($(".alert-box-link-to-vault"),
					$(".exit-panel"), "735px", "580px");
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
