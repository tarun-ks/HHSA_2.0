/* 
 * This method is called after JSP page is ready.
 * 
 * It validate the currency fields on the JSP Page
 */
$(document).ready(function() {
	$("#aoProcurementValue").jqGridCurrency();
	$("#aoContractValue").jqGridCurrency();
	
});

/* 
 * The method is updated as part of release 3.12.0 defect #6602
 * This method is executed when the user clicks the FINISH TASK button
 * on the page. 
 * 
 * It validates:
 * 		the New Fiscal Year's total configured amount should be equal to the Planned amount.
 * 		the sum total of Chart of Accounts budget should be equal to the contract value.
 * 
 * On failure of validation the appropriate VALIDATION message is shown on the page.
 * Updated Method in R4
 * On success the New FY Configuration task is completed.
 */
function finishTaskValidation() {
	//If the user selects 'Cancel' option, error checks are not validated.
	var taskStatus = $("#finishtaskchild").val();
	if("Cancel"==taskStatus){
		return true;
	}else{
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
		var configurableYear = $('#newConfigurableYear').val();
		var colName = "fy" + configurableYear.substring(2);
		
		var totalCOAAmount = $('#table_CoAAllocation tbody tr:eq(1) td:last')
				.html().replace('$', '').replaceAll(',', '');
		var contractAmount = $("#aoContractValue").html().replace('$', '').replaceAll(
				',', '');
		var totalBudgetAmount =  ($('#table_contractBudget1>tbody>tr:eq(1)>td:last').html() != null ? $('#table_contractBudget1>tbody>tr:eq(1)>td:last').html().replace('$', '').replaceAll(',', '') : 0);
		
		var totalFYPlannedAmount = ($("#budgetTabTotalBudgetAmount").html() == null ? 0 : $("#budgetTabTotalBudgetAmount").html().replace(
				'$', '').replaceAll(',', ''));
		var coAGridFYTotalAmount = null;
		
		$('#table_CoAAllocation>tbody>tr:eq(1)>td').each(
				function(index) {
	
					if (getColName(this) == colName) {
						coAGridFYTotalAmount = $(this).html().replace('$', '')
								.replaceAll(',', '');
					}
				});
		
		// QC 9387 R 8.2.0 New FY Config Task throws an error if the amount is less than $1,000 beyond the 1 Fiscal Year
		// when compare values of variables compare them with .valueOf()
		if (totalBudgetAmount.valueOf() != coAGridFYTotalAmount.valueOf()) {
			$("#errorGlobalMsg").show();
			$("#errorGlobalMsg").html(totalBudgetNotEqual);
			return false;
		}else {
			$("#errorGlobalMsg").hide();
		}
		if (contractAmount.valueOf() != totalCOAAmount.valueOf()) {
			$("#errorGlobalMsg").show();
			$("#errorGlobalMsg").html(totalNotEqual);
			return false;
		} else {
			$("#errorGlobalMsg").hide();
		}
		if (coAGridFYTotalAmount.valueOf() != totalFYPlannedAmount.valueOf()) {
			$("#errorGlobalMsg").show();
			$("#errorGlobalMsg").html(totalBudgetNotEqual);
			return false;
		} else {
			$("#errorGlobalMsg").hide();
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

/*
 * This function is called on the Contract Budget tab click.
 * 
 * It calls the getDataForContractBudgetTab() method to get contract budget data
 * for given configurable fiscal year.
 * 
 * After getting data the particular DIV:#contractBudgets1 is shown and
 * DIV:#contractFinancials is made hidden.
 */
function showContractBudgetTab() {
	getDataForContractBudgetTab();
	$("#contractBudgets1").show();
	$("#contractFinancials").hide();
}

/*
 * This method does the AJAX Resource Mapping in BMC Controller with "#hdnNewFYBudgetPageVar"
 * variable.
 * 
 * It fetches the value to be shown on Contract Budget Tab.
 */
function getDataForContractBudgetTab() {
	var viewParameter = "NewFYConfigurationBudgetTab";
	var v_parameter = "&viewTab=" + viewParameter +"&nonEditColname=" + $('#nonEditColname').val();
	var urlAppender = $("#hdnNewFYBudgetPageVar").val() + v_parameter;
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
			//removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
}

/*
 * This function is called on the Contract Financials tab click.
 * 
 * 
 * On method call DIV:#contractBudgets1 is hidden and
 * DIV:#contractFinancials is made visisble.
 */
function showContractFinancialsTab() {
	$("#contractBudgets1").hide();
	$("#contractFinancials").show();
}

// This Overide the JQGrid's Edit Row
var oldEditRow = $.fn.jqGrid.editRow;
$.jgrid.extend({
	editRow: function (iRow,iCol, ed){
		var totalColumn='';
			if(iRow.substring(iRow.lastIndexOf('_') + 1) == 'newrow'){
				totalColumn = getSubGridIds(iRow);
				var tempStringlen = totalColumn.split(',');
				var len = tempStringlen.length;
				for ( var i = 0; i < len; i++) {
					$(this).jqGrid('setColProp',tempStringlen[i], {editable : true});
					if (notEditableForAddRow != null) {
						var tmpnotEditableForAddRow = notEditableForAddRow.split(',');
						for ( var count = 0; count < tmpnotEditableForAddRow.length; count++) {
							if (tempStringlen[i] == tmpnotEditableForAddRow[count]) {
								$(this).jqGrid('setColProp',tempStringlen[i],{editable : false});
							}
						}
					}
				}
   		}
			
			
	if (iRow.indexOf('_newrecord_coa') !== -1){
		totalColumn = getSubGridIds(iRow);
		var tempStringlen = totalColumn.split(',');
		$(this).jqGrid('setColProp',tempStringlen[0], {editable : true});
		$(this).jqGrid('setColProp',tempStringlen[1], {editable : true});
		$(this).jqGrid('setColProp',tempStringlen[2], {editable : true});
	}		
	else if (iRow.indexOf('_newrecord') !== -1){
		totalColumn = getSubGridIds(iRow);
		var tempStringlen = totalColumn.split(',');
			$(this).jqGrid('setColProp',tempStringlen[0], {editable : true});
			$(this).jqGrid('setColProp',tempStringlen[1], {editable : true});
	}else if(iRow == 'new_row'){
		
	}else{
			totalColumn = getSubGridIds(iRow);
			var tempStringlen = totalColumn.split(',');
			$(this).jqGrid('setColProp',tempStringlen[0], {editable : false});
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

//Start: Added in R7 for Cost Center
//This method makes ajax request and displays the data when the contract budget tab is clicked.
function fillCostCenterDetails(){	
	var v_parameter = "&budgetYear="+$('#budgetfiscalYear').text();
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