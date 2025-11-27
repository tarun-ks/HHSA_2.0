//On page load
$(document).ready(function() {
	$("#aoProcurementValue").validateCurrencyOnLoad();
	$("#aoContractValue").validateCurrencyOnLoad();
	$("#budgetTabContractsValue").validateCurrencyOnLoad();
	$("#budgetTabTotalBudgetAmount").validateCurrencyOnLoad();
});

// The method is updated as part of release 3.8.0 defect #6483
// This method is called for client side finish task validation
function finishTaskValidation() {
	var taskStatus = $("#finishtaskchild").val();
	if("Cancel"==taskStatus){
		return true;
	}
	else{
	//R4 Start validation for Budget Template Tab
	var _BudgetCheckBox = false;
	for(var i=1; i<=11; i++){
		if($('#'+i+$.trim($('#fiscalYearId  option:selected').text())).prop('checked')){	
			_BudgetCheckBox = true;
			break;
		}
	}
	if(!_BudgetCheckBox){
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(_BudgetCustomizeError);
		return false;
	}
	//R4 End validation for Budget Template Tab
	var loContractVal=$("#aoContractValue").html();
	var loFormattedContractVal = loContractVal.replaceAll(",", "");

	var loTotalAmount = '';		
	$('#table_CoAAllocation1>tbody>tr:eq(1)>td').each(
	                        function(index) {
	                        	
	                                if (getColName(this) == "total") {
	                                	loTotalAmount = $(this).html().replace('$', '')
	                                                        .replace(/,/g, '');
	                                }
	                        });
	
	
	var loFormattedTotalAmount = loTotalAmount.replaceAll(",", "");
	var loTotalBudgetAmount = $("#budgetTabTotalBudgetAmount").val();
	if (typeof ($("#budgetTabTotalBudgetAmount").val()) == "undefined") {
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(totalBudgetNotEqual);
		return false;
	}
	var loFormattedTotalBudgetAmount = loTotalBudgetAmount.replaceAll(",", "");
	var loProposedAmount = '';		
	$('#table_contractBudget1>tbody>tr:eq(1)>td').each(// updates the proposed amount
	                        function(index) {
	                        	
	                                if (getColName(this) == "proposedBudgetAmount") {
	                                	loProposedAmount = $(this).html().replace('$', '').replace(/,/g, '');
	                                }
	                        });
	
	
	var loFormattedProposedAmount = loProposedAmount.replaceAll(",", "");
	if (new Big(loFormattedTotalAmount).minus(new Big(loFormattedContractVal)) != 0) {
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(totalBudgetCannotExceed);
		return false;
	}else {
		$("#errorGlobalMsg").hide();
	}
	
	 if (new Big(loFormattedProposedAmount).minus(new Big(loFormattedTotalBudgetAmount)) != 0) {
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(totalBudgetNotEqual);
		return false;
	}
	else {
		$("#errorGlobalMsg").hide();
		if($('#servCheckbx')!= null && $('#servCheckbx').prop('checked')){
			saveServices();
		}
	}
	return true;
	var lovalue = $("#aoContractValue").html();
	var loTotalBudgetAmount = $("#budgetTabTotalBudgetAmount").val();
	var loFormattedValue = parseFloat(lovalue.replaceAll(",", ""));
	var temp = '';
	//updates  the element value
	$('#table_CoAAllocation>tbody>tr:nth-child(2)>td').each(function(i) {
		var val = $(this).html().replace('$', '').replace(/,/g, '');
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
	var v = n1.lastIndexOf('.');
	var test = n1 - n1.substring(v + 1);
	if (loFormattedValue != test) {
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(totalNotEqual);
		return false;
	}
	var loFormattedTotalBudgetAmount = loTotalBudgetAmount.replaceAll(",", "");
	var temp1 = '';
	$('#table_contractBudget1>tbody>tr:nth-child(2)>td').each(function(i) {
		var val = $(this).html().replace('$', '').replace(/,/g, '');
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
	var v = n1.lastIndexOf('.');
	var test1 = n1 - n1.substring(v + 1);
	if (loFormattedTotalBudgetAmount != test1) {
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(totalBudgetNotEqual);
		return false;
	} else {
		return true;
	}
	}
}

/*
 * This method is used by finishTaskValidation() method to get the column name of 
 * passed object.
 * 
 * It returns the Column Name for the passed value object.
 */
function getColName(obj) {
	var tempId = $(obj).attr('aria-describedby');
	var n = tempId.lastIndexOf('_');
	return tempId.substring(n + 1);
}

//To show the contract budget tab and hide the contract financials tab
function showContractBudgetTab(FYIVal) {
	var v_parameter = "&selectedFYId="+FYIVal;
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
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});

	$("#contractBudgets1").show();
	$("#contractCOAAndFundingSource").hide();
	$("#oontractFinancials").hide();
}  

//To show the contract financials tab and hide the contract budget tab
function showContractFinancialsTab() {
	$("#contractBudgets1").hide();
	$("#contractCOAAndFundingSource").show();
	$("#oontractFinancials").show();
}

// This method will get Budget Details on the basis of Fiscal Year selected in the dropdown.
function getBugetDetailsByFYI() {
	var fyValue = $("#fiscalYearId").val();
	if(fyValue == '-1')
		{fyValue='tab';}
	showContractBudgetTab(fyValue);
}


//This Overide the JQGrid's Edit Row
var oldEditRow = $.fn.jqGrid.editRow;
$.jgrid.extend({
	editRow: function (iRow,iCol, ed){
		if(iRow == 'new_row'){
			$(this).jqGrid('setColProp','total',{editable : false});
		}
		else if(iRow.indexOf('_newrecord') !== -1){
			var totalColumn = getSubGridIds(iRow);
            var tempStringlen = totalColumn.split(',');
            $(this).jqGrid('setColProp',tempStringlen[0], {editable : true});
		}
		else{
			var totalColumn = getSubGridIds(iRow);
			var tempStringlen = totalColumn.split(',');
			$(this).jqGrid('setColProp',tempStringlen[0], {editable : false});
			$(this).jqGrid('setColProp',tempStringlen[1], {editable : false});
			$(this).jqGrid('setColProp',tempStringlen[2], {editable : false});
			$(this).jqGrid('setColProp','subbudgetName', {editable : false});
		}
       return oldEditRow.call (this, iRow, iCol, ed); 
   }
});

// This function is to get total grid'd Id
function getSubGridIds(rowid){ 
	var tempString='';
	$('#'+rowid+'>td').each(function(i){
		var finalId = getColName(this);
		if(tempString != ''){
			tempString = tempString + ','+ finalId;
		}else{
			tempString = finalId;
		}
	});
return tempString;
}

/*
 * This method is used by finishTaskValidation() method to get the column name of 
 * passed object.
 * 
 * It returns the Column Name for the passed value object.
 */

function getColName(obj) {
	var tempId = $(obj).attr('aria-describedby');
	var n = tempId.lastIndexOf('_');
	return tempId.substring(n + 1);
}

//Start: Added in R7 for Cost Center
//This method makes ajax request and displays the data when the contract budget tab is clicked.
function fillCostCenterDetails(){	
	var v_parameter = "&budgetYear=" + $('#fiscalYearId').val();
	if($('#fiscalYearId').val().indexOf('-1') != 0){
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
	}
}
//End: Added in R7 for Cost Center