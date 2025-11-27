/*javascript written for contract financials page
Updated Method in R4*/

$(document).ready(function() {
	// to format the procurement value and contract value
	$("#aoProcurementValue").jqGridCurrency();
	$("#aoContractValue").jqGridCurrency();
	var isOpenEndedRfpStartEndDateNotSet = $("#isOpenEndedRfpStartEndDateNotSet").val();
	if(isOpenEndedRfpStartEndDateNotSet== "true" || isOpenEndedRfpStartEndDateNotSet == true)
		{$("#contractBudgetsContractConf").hide();}
	else
		{$("#contractBudgetsContractConf").show;}
	
	$("#save").click(function(){
		// start : validation for invalid date
		var _DTStart = true;
		var _DTEnd = true;
		if($('#contractStartDate').val() != '' && isNaN(Date.parse($('#contractStartDate').val())))
			_DTStart = false;
		if($('#contractEndDate').val() != '' && isNaN(Date.parse($('#contractEndDate').val())))
			_DTEnd = false;
		if (!_DTStart)
			$('#errorContractStartDate').html('! Please enter a valid date');
		else
			$('#errorContractStartDate').html('');
		if (!_DTEnd)
			$('#errorContractEndDate').html('! Please enter a valid date');
		else
			$('#errorContractEndDate').html('');
		
		if(_DTStart && _DTEnd ){
			var startDate = new Date( $("#contractStartDate").val());
			var endDate = new Date($("#contractEndDate").val());
			$("#errorContractEndDate").html("");
				if(startDate<endDate){
					document.taskForm1.action = $("#hdnSaveStartEndDate").val();
					document.taskForm1.submit();
				}
				else if($("#contractStartDate").val()!="" && $("#contractEndDate").val()!=""){
					$("#errorContractEndDate").text("! The end date must be after the start date");
				}
			}
		
		// end : validation for invalid date
	});
	
	//This method is called on click of save button for open ended rfp case.
	$("#taskForm1").validate({
		rules: {contractStartDate: {required: true},
				contractEndDate: {required: true}},
		messages: {contractStartDate: {required:"! This field is required."},
					contractEndDate: {required:"! This field is required."}},
								submitHandler: function(form){
									var isValid = true;
									$("input[type='text']").each(function(){
								        if($(this).attr("validate")=='calender'){
								              if(!verifyDate(this)){
								            	  isValid = false;
								              }
								        }
								    });
								},
								  errorPlacement: function(error, element) {
								         error.appendTo(element.parent().parent().find("span.error"));
								  }
	});
});

// Gets the fy budget planned mount for current fiscal year
function getFYBudgetPlannedAmount(currentFiscalYear, contractFirstYear) {
	if (typeof ($("#budgetTabTotalBudgetAmount").val()) == "undefined") {
		return -1;
	}
	rowNumber = 2;
	defaultColumnNumber = 5;
	if (contractFirstYear < currentFiscalYear)
		defaultColumnNumber = defaultColumnNumber
				+ (currentFiscalYear - contractFirstYear);

	plannedAmount = $(
			'#table_CoAAllocation>tbody>tr:nth-child(2)>td:nth-child('
					+ defaultColumnNumber + ')').html().replace('$', '')
			.replace(',', ''); //

	return plannedAmount;
}
/* This method performs check on start date and end date
New Method in R4*/
function contractStartEndDateCheck()
{
	var isValid = true;
	$("input[type='text']").each(function(){
        if($(this).attr("validate")=='calender'){
              if(!verifyDate(this)){
            	  isValid = false;
              }
        }
    });	
	
	if($("#contractStartDate").val()=='')
		{$("#errorContractStartDate").text("! This field is required.");isValid=false;}	
	if($("#contractEndDate").val()=='')
		{$("#errorContractEndDate").text("! This field is required.");isValid=false;}
	
	return isValid;
}
// Validation to check if total contract value is equal to total in chart of
// accounts table
// and also fy budget planned amount should be equal to total in contract budget
// grid.
// If any validation fails, show respective error message.
function finishTaskValidation() {
	
	if(contractStartEndDateCheck())
		{
		var startDate = new Date( $("#contractStartDate").val());
		var endDate = new Date($("#contractEndDate").val());
		if (typeof $("#contractStartDate").val() != "undefined"
				&& typeof $("#contractEndDate").val() != "undefined"
				&& !(startDate < endDate)) {
			$("#errorContractEndDate").text("! The end date must be after the start date");
		}
		else
		{
	//R4 Start validation for Budget Template Tab
	var _BudgetCheckBox = false;
	for(var i=1; i<=8; i++){
		if($('#'+i+$.trim($('#budgetfiscalYear').html())).prop('checked')){	
			_BudgetCheckBox = true;
			break;
		}
	}
	if(!_BudgetCheckBox){
		if (!$('#10' + $.trim($('#budgetfiscalYear').html())).prop('checked')) {
			$("#errorGlobalMsg").show();
			if ($('#9' + $.trim($('#budgetfiscalYear').html())).prop('checked')
					|| $('#11' + $.trim($('#budgetfiscalYear').html())).prop('checked')) {
				$("#errorGlobalMsg").html(_BudgetCustomizeNonMandatoryError);
			} else {
				$("#errorGlobalMsg").html(_BudgetCustomizeError);
			}
			return false;
		}
	}
	//R4 End validation for Budget Template Tab
	var lovalue = $("#aoContractValue").html();
	var loFormattedValue = parseFloat(lovalue.replace("$","").replaceAll(",", ""));
	var temp = '';
	$('#table_CoAAllocation>tbody>tr:nth-child(2)>td').each(function(i) {
		var val = $(this).html().replace('$', '').replaceAll(',', '');
		if ($.isNumeric(val)) {
			if (temp != '') {
				temp = temp + ',' + val;
			} else {
				temp = val;
			}
		}
		val = 0;
	});
	var n = temp.lastIndexOf(',');
	var n1 = temp.substring(n + 1);
	if (loFormattedValue != n1) {
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(totalNotEqual);
		return false;
	}
	if (typeof ($("#budgetTabTotalBudgetAmount").val()) == "undefined") {
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(totalBudgetNotEqual);
		return false;
	}
	var loTotalBudgetAmount = $("#budgetTabTotalBudgetAmount").val();
	var loFormattedTotalBudgetAmount = parseFloat(loTotalBudgetAmount
			.replaceAll(",", ""));
	var temp1 = '';
	$('#table_contractBudget1>tbody>tr:nth-child(2)>td').each(function(i) {
		var val = $(this).html().replace('$', '').replaceAll(',', '');
		if ($.isNumeric(val)) {
			if (temp1 != '') {
				temp1 = temp1 + ',' + val;
			} else {
				temp1 = val;
			}
		}
		val = 0;
	});
	var n = temp1.lastIndexOf(',');
	var n1 = temp1.substring(n + 1);
	if (loFormattedTotalBudgetAmount != n1) {
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(totalBudgetNotEqual);
		return false;
	} 
	else 
	{		
		if($('#servCheckbx')!= null && $('#servCheckbx').prop('checked')){
			saveServices();
		}
		return true;
	}
	}
	}
}

// To show the contract budget tab and hide the contract financials tab
function showContractBudgetTab1(currentFiscalYear, contractFirstYear) {
	var fYBudgetPlannedAmount = getFYBudgetPlannedAmount(currentFiscalYear,
			contractFirstYear);
	var v_parameter = "fYBudgetPlannedAmount=" + fYBudgetPlannedAmount;
	var urlAppender = $("#hdnCcontractBudgetPageVar").val()
			+ "?fYBudgetPlannedAmount=" + fYBudgetPlannedAmount;

	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#contractBudgets1").html(e);
					removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});

	$("#contractBudgets1").show();
	$("#contractCOAAndFundingSource").hide();
	$("#oontractFinancials").hide();
	clearAndHideError();
}

//This method is called on click of save button for open ended rf case.
function saveStartEndDateForOpenEndedRfp()
{
	document.taskForm1.action = $("#hdnSaveStartEndDate").val();
	document.taskForm1.submit();
} 

//To show the contract budget tab and hide the contract financials tab
function showContractBudgetTabNow() {
	fillContractBudgetTab();
	$("#contractBudgets1").show();
	$("#contractCOAAndFundingSource").hide();
	$("#oontractFinancials").hide();
	clearAndHideError();
}

//This method makes ajax request and displays the data when the contract budget tab is clicked.
function fillContractBudgetTab(){	
	var v_parameter = "";
	var urlAppender = $("#hdnCcontractBudgetPageVar").val();

	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#contractBudgets1").html(e);
			//Start: Added in R7 for Cost Center
			fillCostCenterDetails();
			//End: Added in R7 for Cost Center
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
	clearAndHideError();
}

// To show the contract financials tab and hide the contract budget tab
function showContractFinancialsTab() {
	$("#contractBudgets1").hide();
	$("#contractCOAAndFundingSource").show();
	$("#oontractFinancials").show();
	clearAndHideError();
}

function fillAndShowOverlay() {

}

// This function Clears the error message before every fresh action on the page
// (basically on Grids and Tabs)
function clearAndHideError() {
	$("#errorGlobalMsg").html("");
	$("#errorGlobalMsg").hide();
}


//This Overide the JQGrid's Edit Row
var oldEditRow = $.fn.jqGrid.editRow;
$.jgrid.extend({
	editRow: function (iRow,iCol, ed){
		if(iRow == 'new_row'){
			$(this).jqGrid('setColProp','total',{editable : false});
		}
       return oldEditRow.call (this, iRow, iCol, ed); 
   }
});

// This function will return the column's bean name
function getColName(obj){
		var tempId = $(obj).attr('aria-describedby');
		var n = tempId.lastIndexOf('_');
	return tempId.substring(n + 1);	
}

// Start: Added in R7 for Cost Center
//This method makes ajax request and displays the data when the contract budget tab is clicked.
function fillCostCenterDetails(){	
	var v_parameter = "";
	var urlAppender = $("#getCostCenterDetails").val();

	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#costCenterDiv").html(e);
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
	clearAndHideError();
}
//End: Added in R7 for Cost Center