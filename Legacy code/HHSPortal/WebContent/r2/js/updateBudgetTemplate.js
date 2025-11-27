$(document).ready(function() {
	
	//Logic for checkBox checked and disabled.
	var entryTypeData = $('#entryTypeId').val();
	entryTypeData = entryTypeData.replace('[','').replace(']','');
	if(entryTypeData != ''){
		entryTypeData = entryTypeData.split(',');
		for(var i=0; i<entryTypeData.length; i++){
			var _tab = $.trim(entryTypeData[i]);
			_tab = _tab.split(':');
			var _temp = _tab[0];
			$('#'+_temp).prop('checked', true);
				$('#'+_temp).prop("disabled", true);
		}
	}else{
		//For R3 Budget all checkBox checked and disabled
		for(var i=1; i<12; i++){
			$('#'+i).prop('checked', true);
			$('#'+i).prop("disabled", true);
		}
	}
});

//On Click of Update Button
function UpdateTemplate() {
	var _cid = $('#contractId').val();
	var _bid = $('#budgetId').val();
	var _fyid = $('#fiscalYearId').val();
	var _checkedEntryType='';
	for(var i=1; i<=11; i++){
		if(!$('#'+i).is(':disabled')){
			if($('#'+i).is(":checked")){
				if(_checkedEntryType == ''){
					_checkedEntryType = i;
				}else{
					_checkedEntryType = _checkedEntryType + ',' + i;
				}
			}
		}
	}
	var v_parameter = "contractId=" + _cid + "&id=" + _checkedEntryType + "&budgetYear=" + _fyid + "&budgetId=" + _bid;
	var urlAppender = $("#hdnUpdateBudgetTemplater").val();
	
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data :v_parameter,
		success : function(e) {
			removePageGreyOut();
			$(".overlay").closeOverlay();
			window.location.href = $("#duplicateRender").val();
		}
	});
}

//On Click of Cancel Button
function cancelUpdateTemplate(){
	$(".overlay").closeOverlay();
}