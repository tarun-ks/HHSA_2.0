/**
 * ===========================================================
 * This file contains the methods that will handle the events
 * on Task Inbox Screen.
 * ===========================================================
 */

/**
 * This method open Display filter window on click of filter Tasks Button
 * 
 */
function setVisibility(id, visibility) {
	document.getElementById(id).style.display = visibility;
	callBackInWindow("closePopUp");
}
suggestionVal = "";
isValid = false;
$("#filtersBtn").click(function() {
	$("#griddiv").show();
	$("#reassigndiv").show();
});
var taskArray = new Array();
// AJAX Method
$(function() {
	$('#tabs').tabs();
	$('#newTabs').tabs();

	$('#dialog').dialog({
		autoOpen : false,
		width : 600,
		buttons : {
			"Ok" : function() {
				$(this).dialog("close");
			},
			"Cancel" : function() {
				$(this).dialog("close");
			}
		}
	});
	$('#dialog_link, ul#icons li').hover(function() {
		$(this).addClass('ui-state-hover');
	}, function() {
		$(this).removeClass('ui-state-hover');
	});
});
var selected = new Array();
/**
*  on page load
*  Updated Method in R4
* */
$(document).ready(
		function() {
			$("input:checkbox[name=type]:checked").each(function() {
				selected.push($(this).val());
			});
			$('#reassigntouser').change(function() {
				enableSubmit();
			});
			$('#providername').keyup(function() {
				var uoDiAppartenenza = $('#providername').val();
				isValid = isAutoSuggestValid(uoDiAppartenenza, suggestionVal);

				if (isValid) {

				} else {

				}
			});
			document.getElementById("filtersBtn").disabled = true;
			// Auto complete
			var onAutocompleteSelect = function(value, data) {
				isValid = true;
			}

			var options = {
				serviceUrl : $("#contextPathSession").val()
						+ '/AutoCompleteServlet.jsp?selectedpage=inbox',
				width : 145,
				minChars : 3,
				maxHeight : 100,
				delimiter : null,
				onSelect : onAutocompleteSelect,
				clearCache : true,
				deferRequestBy : 0,
				params : {
					city : $("#providername").val()
				}
			};

			var a1 = $('#providername').autocomplete(options);
			
			/**
			 * ***** Begin QC 5446
			 * Competition Pool type ahead 
			 */
			if($("#CompetitionPoolTitle").val() == "" && $("#procurementId").val() != "P"){
				$("#CompetitionPoolTitle").attr("disabled", true);
			}else{
				typeHeadSearch($('#CompetitionPoolTitle'), $("#hiddenFetchTypeAheadNameList")
						.val()+ "&QueryId=fetchCompetitionPoolTitleList&key=COMPETITION_POOL_ID&value=COMPETITION_POOL_TITLE&procurementTitle="+$("#ProcurementTitle").val(), null, null, null, "competitionPoolId");
			}
			
			$('#ProcurementTitle').keyup(function(evt) {
				if (suggestionVal.length > 0 && 3 <= $('#ProcurementTitle').val().length)
				var keyCode = evt ? (evt.which ? evt.which : evt.keyCode)
						: event.keyCode;
				if (keyCode != 13) {
					$("#procurementId").val("");
					$("#CompetitionPoolTitle").val("");
					$("#competitionPoolId").val("");
				}
			});
			/******  End QC5446  *********/
		});

/**
 * ******  QC 5446 ***********
 * For type ahead & disabling Competition Pool 
 */
function callBackProcurementTitleOnSelect(){
	if($("#procurementId").val() == "P"){
		$('#CompetitionPoolTitle').unbind().keyup(function(e){
			if(e.keyCode!=37 && e.keyCode!=38 && e.keyCode!=39 && e.keyCode!=40)
				replaceAllExceptAllowedChar(this);
		}).focusout(function(){
			replaceAllExceptAllowedChar(this);
		});
		typeHeadSearch($('#CompetitionPoolTitle'), $("#hiddenFetchTypeAheadNameList")
				.val()+ "&QueryId=fetchCompetitionPoolTitleList&key=COMPETITION_POOL_ID&value=COMPETITION_POOL_TITLE&procurementTitle="+$("#ProcurementTitle").val(), null, null, null, "competitionPoolId");
	}else{
		$('#CompetitionPoolTitle').attr("disabled", true);
	}
}
/******  End QC5446  *********/

/**
 * This method compare to values and called from AJAX method
 * 
 */
function isAutoSuggestValid(uoDiAppartenenza, suggestionVal) {
	var uoValid = false;

	if (suggestionVal.length > 0) {
		for (i = 0; i < suggestionVal.length; i++) {
			var arrVal = suggestionVal[i].toUpperCase();
			if (arrVal == uoDiAppartenenza.toUpperCase()) {
				uoValid = true;
				break;
			}
		}
	}
	return uoValid;
}

/**
 * This method Enable or Disable all check boxes on click on checkBox in Header
 * 
 */
function selectAllCheck() {
	if (document.myinboxform.selectAll.checked == true) {
		if (document.myinboxform.check.length > 1) {
			for ( var a = 0; a < document.myinboxform.check.length; a++) {
				document.myinboxform.check[a].checked = true;
			}
		} else {
			document.myinboxform.check.checked = true;
		}
		if (document.getElementById("reassigntouser").selectedIndex != 0) {
			document.getElementById("reassignId").disabled = false;
		}
	} else {
		if (document.myinboxform.check.length > 1) {
			for ( var a = 0; a < document.myinboxform.check.length; a++) {
				document.myinboxform.check[a].checked = false;
			}
		} else {
			document.myinboxform.check.checked = false;
		}
		document.getElementById("reassignId").disabled = true;
	}
}

/**
 * This method Enable or Disable Reassign button on basis of selected check box
 * and value in user list drop down.
 * 
 */
function enableSubmit() {
	var chks = document.getElementsByName('check');
	var hasChecked = false;
	var hasCheckedAll = true;
	for ( var i = 0; i < chks.length; i++) {

		if (chks[i].checked) {
			hasChecked = true;
		} else {
			hasCheckedAll = false;
			document.myinboxform.selectAll.checked = false;
		}
		taskArray[i] = chks[i].value;
	}
	if (hasCheckedAll) {
		document.myinboxform.selectAll.checked = true;
	}
	if (hasChecked
			&& document.getElementById("reassigntouser").selectedIndex != 0) {
		document.getElementById("reassignId").disabled = false;
	} else {
		document.getElementById("reassignId").disabled = true;
	}
}
var url = '';

/**
 * Method submit the form and pass required values as parameter.
 * 
 */
function submitForm(taskid, isTaskLock, isManagerTask, lsStatus) {
// Start Added in R5
	if(($("#awardTaskName").text()=="Approve PSR") || ($("#awardTaskName").text()=="Approve Award Amount")){
		document.myinboxform.action = document.myinboxform.action + '&taskid='
		+ taskid + '&controller_action=inboxControllerExtended' + '&taskType=' + $("#awardTaskName").text();
		document.myinboxform.submit();
	}else{
	// End Added in R5
	document.myinboxform.action = document.myinboxform.action + '&taskid='
			+ taskid + '&isTaskLock=' + isTaskLock + '&isManagerTask='
			+ isManagerTask + '&taskStatus=' + lsStatus + '&awardTaskName='
			+ $("#awardTaskName").text();
	document.myinboxform.submit();
	
	}
}

/**
 * Method submit the form and pass required values as parameter.
 * 
 */
function submitForm1(actionurl, taskid) {
	document.myinboxform.action = actionurl + '&taskid=' + taskid;
	document.myinboxform.submit();
}
function onload(url1) {
	url = url1;
}
/**
 * Method submit the form on click of pagination click and pass page number.
 * 
 */
function paging(pageNumber) {
	document.myinboxform.action = document.myinboxform.action + "&pageIndex="
			+ pageNumber + "&action=paging";
	document.myinboxform.submit();
}
/**
 * Method submit the form on click of column header click and pass column name
 * to sort.
 * 
 */
function sort(columnName, sortType) {
	document.myinboxform.action = document.myinboxform.action + "&columnName="
			+ columnName + "&sortType=" + sortType + "&action=sort"
			+ '&paging_sorting=true';
	document.myinboxform.submit();
}
/**
 * Method submit the form on click of filer button.
 * 
 */
function filtertask() {
	
	/******  Begin QC5446  *********/
	var procurementTitle = $("#ProcurementTitle").val();
	if (null != procurementTitle && procurementTitle != '') {
		var length = procurementTitle.length;
		if (length < 5) {
			$('#ProcurementTitle').parent().next().html(
					"! You must enter 5 or more characters");
			return false;
		}
	}
	
	var CompetitionPoolTitle = $("#CompetitionPoolTitle").val();
	if (null != CompetitionPoolTitle && CompetitionPoolTitle != '') {
		var length = CompetitionPoolTitle.length;
		if (length < 5) {
			$('#CompetitionPoolTitle').parent().next().html(
					"! You must enter 5 or more characters");
			return false;
		}
	}
	/******  End QC5446  *********/
	
	var isValid = true;
	$("input[type='text']").each(function() {
		if ($(this).attr("validate") == 'calender') {
			if (!verifyDate(this)) {
				isValid = false;
			}
		}
	});
	if (isValid) {
		pageGreyOut();
		document.myinboxform.action = document.myinboxform.action
				+ "&filteristrue=yes";
		document.myinboxform.submit();
	}
}
/**
 * Method enable or disable filter button
 * 
 */
function enableFilter() {
	if (document.getElementById("tasktype").selectedIndex == 0) {
		document.getElementById("filtersBtn").disabled = true;
	} else {
		document.getElementById("filtersBtn").disabled = false;
	}
	
	/******  Begin QC5446  *********/
	// Start Added in R5
	var docType = document.getElementById("tasktype").value;
	if (docType == "Approve Award" || docType == "Approve PSR" || docType == "Approve Award Amount") {
	// End Added in R5
		document.getElementById("providername").disabled = true;
		document.getElementById("ProcurementTitle").disabled = false;
		document.getElementById("agencyName").disabled = false;
		typeHeadSearch($('#ProcurementTitle'), $("#hiddenFetchTypeAheadNameList").val()+ "&QueryId=fetchProcurementTitleList&key=PROCUREMENT_TYPE&value=PROCUREMENT_TITLE", 
				"CompetitionPoolTitle", null, null, "procurementId", "callBackProcurementTitleOnSelect");
	} else {
		document.getElementById("providername").disabled = false;
		document.getElementById("ProcurementTitle").disabled = true;
		document.getElementById('ProcurementTitle').value = "";
		document.getElementById("CompetitionPoolTitle").disabled = true;
		document.getElementById('CompetitionPoolTitle').value = "";
		document.getElementById("agencyName").disabled = true;
		document.getElementById('agencyName').value = "";
	}
	/******  End QC5446  *********/
}

/**
 * Method clear the selected filter values in filter
 * 
 */
function clearfilter() {
	document.getElementById('tasktype').value = "";
	document.getElementById('status').value = "";
	document.getElementById('dateto').value = "";
	document.getElementById('dateassignedfrom').value = "";
	document.getElementById('dateassignedto').value = "";
	document.getElementById('datefrom').value = "";
	document.getElementById('providername').value = "";
	/******  Begin QC5446  *********/
	document.getElementById('ProcurementTitle').value = "";
	document.getElementById('ProcurementTitle').disabled = true;
	document.getElementById('CompetitionPoolTitle').value = "";
	document.getElementById('CompetitionPoolTitle').disabled = true;
	document.getElementById('agencyName').value = "";
	document.getElementById('agencyName').disabled = true;
	document.getElementById('datefrom').value = "";
	document.getElementById('filtersBtn').disabled = true;
	$(".error").text("");
	/******  End QC5446  *********/
	$("input[type='text']").each(function() {
		if ($(this).attr("validate") == 'calender') {
			$(this).parent().next().html("");
		}
	});
}

/**
 * Method to display error or success Message
 * 
 */
function showMe(it, box) {
	if (box.id == 'box') {
		vis = "none";
	} else {
		vis = "block";
	}
	document.getElementById(it).style.display = vis;
}

/**
 * Function sets the maximum length of Textarea.
 * updaed in R5
 */
function setMaxLength(obj, maxlimit) {
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}