/*javascript written for amendment configuration page
 On page load
Updated Method in R4*/
$(document).ready(function() {

	$("#contractBudgets1").hide();
	$("#contractFinancials").show();

	$("#aoContractValue").jqGridCurrency();
	$("#aoAmendmentValue").jqGridCurrency();

if($("#aoPosAmendmentValue").html() != null){
	$("#aoPosAmendmentValue").jqGridCurrency();
}
if($("#aoNegAmendmentValue").html() != null){
	$("#aoNegAmendmentValue").jqGridCurrency();
}
	

});

// Gets the fy budget planned mount for current fiscal year
function getFYBudgetPlannedAmount(firstFiscalYear, totalNoOfYears) {
	var totalArray = new Array();
	
	$('#table_CoAAllocationAmendment>tbody>tr:eq(1)>td').each(function(index) {

				var plannedAmount = 0;
				if(index > 3){
					if(getColName(this) != 'total'){
						 plannedAmount = $(this).html().replace('($', '-').replace(')', '').replace('$', '').replaceAll(',', '');
						 if(new Big(plannedAmount) != 0){
							 totalArray.push(firstFiscalYear);
						 }
							firstFiscalYear++;
					}
				}
			});
	return totalArray;
}


// This function will return the column's bean name

function getColName(obj) {
	var tempId = $(obj).attr('aria-describedby');
	var n = tempId.lastIndexOf('_');
	return tempId.substring(n + 1);
}

// Validation to check if total contract value is equal to total in chart of
// accounts table
// and also fy budget planned amount should be equal to total in contract budget
// grid.
// If any validation fails, show respective error message.
function finishTaskValidation() {
	var lovalue = $("#aoAmendmentValue").html();
	var loFormattedValue = lovalue.replace('$', '').replaceAll(',', '').replace('(','-').replace(')','');
	var temp = '';
	$('#table_CoAAllocationAmendment>tbody>tr:nth-child(2)>td').each(function(i) {
		var val = $(this).html().replace('$', '').replaceAll(',', '').replace('(','-').replace(')','');
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
		$("#taskErrorDiv").show();
		$("#taskErrorDiv").html(totalNotEqual);
		return false;
	}
	else{
		if ($('#servCheckbx') != null && $('#servCheckbx').prop('checked')) {
			saveServices();
		}
		return true;
	}
}

// To show the contract budget tab and hide the contract financials tab
function showContractBudgetTab(firstFiscalYear, totalNoOfYears, fyYear) {
	var totalFyArray;
	if(null != firstFiscalYear){
	totalFyArray = getFYBudgetPlannedAmount(firstFiscalYear,
			totalNoOfYears);
	}
	var urlAppender = $("#hdnCcontractBudgetPageVar").val()
			+ "&totalFyArray=" + totalFyArray + "&fyYear=" + fyYear;

	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			$("#contractBudgets1").html(e);
			//Start: Added in R7 for Cost Center
			fillCostCenterDetails(fyYear);
			//End: Added in R7 for Cost Center
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});

	$("#contractBudgets1").show();
	$("#contractFinancials").hide();

	clearAndHideError();
}

// To show the contract financials tab and hide the contract budget tab
function showContractFinancialsTab() {
	$("#contractBudgets1").hide();
	$("#contractFinancials").show();
	clearAndHideError();
}

// This function Clears the error message before every fresh action on the page
// (basically on Grids and Tabs)
function clearAndHideError() {
	$("#taskErrorDiv").html("");
	$("#taskErrorDiv").hide();
}

// this function gets the budget details against the total no. of financial years 
function getBudgetDetailsByFYI(firstFiscalYear, totalNoOfYears) {
	var fyValue = $("#fiscalYearId").val();
	if(fyValue == '-1')
		{fyValue='false';}
	showContractBudgetTab(firstFiscalYear,totalNoOfYears,fyValue);
}
//Start: Added in R7 for Cost Center
//This method makes ajax request and displays the data when the contract budget tab is clicked.
function fillCostCenterDetails(fyYear){	
	if(fyYear != 'false'){
	var v_parameter = "&budgetYear="+ fyYear;
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
	else{
		removePageGreyOut();
	}
}
