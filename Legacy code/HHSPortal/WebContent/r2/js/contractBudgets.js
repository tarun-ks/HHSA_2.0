// On page load
$(document).ready(function() {
	$("#budgetTabContractsValue").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
	$("#budgetTabTotalBudgetAmount").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
	$("#currentFYPlannedAmount").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
	$("#amendmentFYPlannedAmount").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
	$("#newFYPlannedAmount").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
	// to format the procurement value and contract value
	$("#aoContractValue").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
	$("#aoAmendmentValue").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
	$("#aoNewContractValue").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
});


//Gets the fy budget planned mount for current fiscal year
function getFYBudgetPlannedAmount(firstFiscalYear, totalNoOfYears) {
	var totalArray = new Array();
	
	$('#table_CoAAllocationAmendment>tbody>tr:eq(1)>td').each(function(index) {
				var plannedAmount = 0;
				if(index > 3){
					if(getColName(this) != 'total'){
						 plannedAmount = $(this).html().replace('$', '').replaceAll(',', '');
						 if(parseFloat(plannedAmount) > 0){
							 totalArray.push(firstFiscalYear);
						 }
							firstFiscalYear++;
					}
				}
			});
	return totalArray;
}

//this function gets the budget details against the total no. of financial years 
function getBudgetDetailsByFYI(firstFiscalYear, totalNoOfYears) {
	var fyValue = $("#fiscalYearId").val();
	if(fyValue == '-1')
		{fyValue='false';}
	showContractBudgetTab(firstFiscalYear,totalNoOfYears,fyValue);
}

//To show the contract budget tab and hide the contract financials tab
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
			// alert(e);
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});

	$("#contractBudgets1").show();
	$("#contractFinancials").hide();
	clearAndHideError();
}


// This method gets the column name
function getColName(obj) {
	var tempId = $(obj).attr('aria-describedby');
	var n = tempId.lastIndexOf('_');
	return tempId.substring(n + 1);
}

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

//method to view printer friendly version
function View(subBudgetID) {
	var a_href = $("#printerViewCB"+subBudgetID).attr('href') + "&removeMenu=";
	window.open(a_href);
}

/**
* This function called On loadcomplete to copy fringe benefit rate on jqgrid(fringe benefit) header
* Added in Release 6
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


	
