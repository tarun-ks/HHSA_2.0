<%-- JSP added for R4 --%>
<script type="text/javascript">
	var _checktable = '${budgetfiscalYear}';
	var _tmpSelectedYear = '';
	if(_checktable.indexOf('[') !== -1){
		$('#budgetCustomizeTab1').hide();
		_tmpSelectedYear='${selectedFYId}';
	}else{
		_tmpSelectedYear='${budgetfiscalYear}';
		$('#budgetCustomizeTab2').hide();
	}

	var entryTypeData = '${entryTypeId}';
	entryTypeData = entryTypeData.replace('[','').replace(']','');
	if(entryTypeData != ''){
		entryTypeData = entryTypeData.split(',');
		for(var i=0; i<entryTypeData.length; i++){
			var _tab = $.trim(entryTypeData[i]);
			_tab = _tab.split(':');
			var _temp = _tab[0]+_tmpSelectedYear;
			$('#'+_temp).prop('checked', true);
			//if 1 then checkbox is checked and disabled
			if(_tab[1] == '1'){
				$('#'+_temp).prop("disabled", true);
			}
		}
	}
	
	if("${aoContractData.amendmentType}" != "" && "${aoContractData.amendmentType}" == "negative")
			for(var i=1; i<12; i++)
				$('#'+i+_tmpSelectedYear).prop("disabled", true);
	
	function onEntityTypeIdCheckBoxClick(_checkBoxId) {
		var _budgetYear = _tmpSelectedYear;
		
		var _operation = '';
		if($('#'+_checkBoxId).is(":checked")){
			_operation=true;
		}else{
			_operation=false;
		}
		
		var v_parameter = "operation=" + _operation + "?id=" + _checkBoxId + "?budgetYear=" + _budgetYear;
		var urlAppender = $("#hdnBudgetCustomizedVar").val()
		+ "&OPERATION=" + _operation + "&id=" + _checkBoxId + "&budgetYear=" + _budgetYear + "&screenName="+screenName;
		
		pageGreyOut();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data :"",
			success : function(e) {
				removePageGreyOut();
			}
		});
	}
	
</script>

<br>
<br>
<B id="budgetTemplate">Budget Template</B>
<div>
	Please select the budget template for the Fiscal Year Budget. Please note that<br> budget categories cannot be removed once added.
</div>
<br>
<br>
<table id="budgetCustomizeTab1">
	<tr>
		<td><input type="checkbox" id="1${budgetfiscalYear}" name="1${budgetfiscalYear}" onchange="onEntityTypeIdCheckBoxClick(1${budgetfiscalYear});"/>Personnel Services</td>
		<td><input type="checkbox" id="5${budgetfiscalYear}" name="5${budgetfiscalYear}" onchange="onEntityTypeIdCheckBoxClick(5${budgetfiscalYear});"/>Rent</td>
		<td><input type="checkbox" id="9${budgetfiscalYear}" name="9${budgetfiscalYear}" onchange="onEntityTypeIdCheckBoxClick(9${budgetfiscalYear});"/>Unallocated Funds</td>
	</tr>
	<tr>
		<td><input type="checkbox" id="2${budgetfiscalYear}" name="2${budgetfiscalYear}" onchange="onEntityTypeIdCheckBoxClick(2${budgetfiscalYear});"/>Operations and Support</td>
		<td><input type="checkbox" id="6${budgetfiscalYear}" name="6${budgetfiscalYear}" onchange="onEntityTypeIdCheckBoxClick(6${budgetfiscalYear});"/>Contracted Services</td>
		<td><input type="checkbox" id="10${budgetfiscalYear}" name="10${budgetfiscalYear}" onchange="onEntityTypeIdCheckBoxClick(10${budgetfiscalYear});"/>Indirect Rate</td>
	</tr>
	<tr>
		<td><input type="checkbox" id="3${budgetfiscalYear}" name="3${budgetfiscalYear}" onchange="onEntityTypeIdCheckBoxClick(3${budgetfiscalYear});"/>Utilities</td>
		<td><input type="checkbox" id="7${budgetfiscalYear}" name="7${budgetfiscalYear}" onchange="onEntityTypeIdCheckBoxClick(7${budgetfiscalYear});"/>Rate</td>
		<td><input type="checkbox" id="11${budgetfiscalYear}" name="11${budgetfiscalYear}" onchange="onEntityTypeIdCheckBoxClick(11${budgetfiscalYear});"/>Program Income</td>
	</tr>
	<tr>
		<td><input type="checkbox" id="4${budgetfiscalYear}" name="4${budgetfiscalYear}" onchange="onEntityTypeIdCheckBoxClick(4${budgetfiscalYear});"/>Professional Services</td>
		<td><input type="checkbox" id="8${budgetfiscalYear}" name="8${budgetfiscalYear}" onchange="onEntityTypeIdCheckBoxClick(8${budgetfiscalYear});"/>Milestone</td>
		<td></td>
	</tr>
</table>
<table id="budgetCustomizeTab2">
	<tr>
		<td><input type="checkbox" id="1${selectedFYId}" name="1${selectedFYId}" onchange="onEntityTypeIdCheckBoxClick(1${selectedFYId});"/>Personnel Services</td>
		<td><input type="checkbox" id="5${selectedFYId}" name="5${selectedFYId}" onchange="onEntityTypeIdCheckBoxClick(5${selectedFYId});"/>Rent</td>
		<td><input type="checkbox" id="9${selectedFYId}" name="9${selectedFYId}" onchange="onEntityTypeIdCheckBoxClick(9${selectedFYId});"/>Unallocated Funds</td>
	</tr>
	<tr>
		<td><input type="checkbox" id="2${selectedFYId}" name="2${selectedFYId}" onchange="onEntityTypeIdCheckBoxClick(2${selectedFYId});"/>Operations and Support</td>
		<td><input type="checkbox" id="6${selectedFYId}" name="6${selectedFYId}" onchange="onEntityTypeIdCheckBoxClick(6${selectedFYId});"/>Contracted Services</td>
		<td><input type="checkbox" id="10${selectedFYId}" name="10${selectedFYId}" onchange="onEntityTypeIdCheckBoxClick(10${selectedFYId});"/>Indirect Rate</td>
	</tr>
	<tr>
		<td><input type="checkbox" id="3${selectedFYId}" name="3${selectedFYId}" onchange="onEntityTypeIdCheckBoxClick(3${selectedFYId});"/>Utilities</td>
		<td><input type="checkbox" id="7${selectedFYId}" name="7${selectedFYId}" onchange="onEntityTypeIdCheckBoxClick(7${selectedFYId});"/>Rate</td>
		<td><input type="checkbox" id="11${selectedFYId}" name="11${selectedFYId}" onchange="onEntityTypeIdCheckBoxClick(11${selectedFYId});"/>Program Income</td>
	</tr>
	<tr>
		<td><input type="checkbox" id="4${selectedFYId}" name="4${selectedFYId}" onchange="onEntityTypeIdCheckBoxClick(4${selectedFYId});"/>Professional Services</td>
		<td><input type="checkbox" id="8${selectedFYId}" name="8${selectedFYId}" onchange="onEntityTypeIdCheckBoxClick(8${selectedFYId});"/>Milestone</td>
		<td></td>
	</tr>
</table>
<!-- Start: Added in R7 for Cost Center -->
<div id="costCenterDiv">
</div>
<!-- End: Added in R7 for Cost Center -->