var checkedCheckBoxes = "";
groupClass = "selectedSetting";
otherClass = "noSelectSetting";
groupCSS = "." + groupClass;
otherCSS = "." + otherClass;
suggestionVal = "";
var isValid = false;
/* this function is invoked on load of jsp and set default parameters
Updated Method in R4*/
$(document).ready(
		function() {
			$(".favoriteIds:checked").each(function(){
				checkedCheckBoxes = checkedCheckBoxes + $(this).val() + ",";
			});
			// function provides for font change
			$("a[id!='smallA'][id!='mediumA'][id!='largeA'][id!='helpIcn']").click(function(e) {
				var $self=$(this);
				if($(this).hasClass('sort-ascending') || $(this).hasClass('sort-descending') || $(this).hasClass('sort-default') 
						|| $(this).parent().parent().parent().hasClass('paginationWrapper')
						|| $(this).hasClass('procTitleLink')){
					return true;
				}
				var isSame = true;
				var tempCheckedCheckBoxes = "";
				$(".favoriteIds:checked").each(function(){
					tempCheckedCheckBoxes = tempCheckedCheckBoxes + $(this).val() + ",";
				});
				if(checkedCheckBoxes != tempCheckedCheckBoxes)
						isSame = false;
				if(!isSame){
					e.preventDefault();
					// displays dialogue box when cancel is clicked with unsaved data.
					$('<div id="dialogBox"></div>').appendTo('body')
					.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
					.dialog({
						modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
						width: 'auto', modal: true, resizable: false, draggable:false,
						dialogClass: 'dialogButtons',
						buttons: {
							OK: function () {
								document.location = $self.attr('href');
								$(this).dialog("close");
							},
							Cancel: function () {
								$(this).dialog("close");
							}
						},
						close: function (event, ui) {
							$(this).remove();
						}
					});
					$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
				}
			});
			
			//disable dates fields for open ended RFPs 
			$("#isOpenEndedRFP").click(function(){
				enableDisabledDates($(this));
			});
			
			enableDisableDefaultParams();
			$(groupCSS + ", " + otherCSS).click(function() {
				var changedNode = $(this);
				enableDisableCheckBoxFull(changedNode);
			});// Typehead for procurement EPin
			typeHeadSearch($('#procurementEpin'), $("#procurementEpinList")
					.val()
					+ "&epinQueryId=fetchProcurementEpinList", null,
					"typeHeadCallBack", $("#epinError"));
			$("#agency").change(function() {
				agency = $("#agency").val();
				if (agency == 'All NYC Agencies') {
					$("#programName").val("");
					$("#programName").prop('disabled', true);
				} else {
					getProgramNameList();
				}
				enableDisableDefaultFilter();
			});
			$('#procurementEpin').bind('copy paste', function (e) {
			    e.preventDefault();
			});//saves favorites
			$("#saveUpdatesToFavorite").click(function(){
				pageGreyOut();
				var favoriteIds = "";
				$(".favoriteIds:checked").each(function(){
					favoriteIds = favoriteIds + $(this).val()+",";
				});
				var nonFavoriteIds = "";
				$(".favoriteIds:not(:checked)").each(function(){
					nonFavoriteIds = nonFavoriteIds + $(this).val()+",";
				});
				$("#favoriteIds").val(favoriteIds);
				$("#nonFavoriteIds").val(nonFavoriteIds);
				$("#procform").attr("action", $("#procform").attr("action")
						+ "&submit_action=saveFavorites");
				pageGreyOut();
				document.procform.submit();
			});
			//displays favorite
			$("#displayFavoritesOnly").click(function(){
				$("#isFavoriteDisplayed").val("true");
				$("#procform")
				.attr(
						"action",
						$("#procform").attr("action")
								+ "&submit_action=showFavorites");
				pageGreyOut();
				document.procform.submit();
			});
			//displays all
			$("#displayAll").click(function(){
				$("#isFavoriteDisplayed").val("false");
				$("#procform")
				.attr(
						"action",
						$("#procform").attr("action")
								+ "&submit_action=showFavorites");
				pageGreyOut();
				document.procform.submit();
			});
			
		
		});


function filterCleaning(){
	clearProcurementFilter();
	
	$(".selectedSetting").attr('checked', false);
	$(".selectedSetting").attr('disabled', false);
	$(".noSelectSetting").attr('checked', false);
	$(".noSelectSetting").attr('disabled', false);
	
	$(".providerStatusClass").attr('checked', false);
	
}

// This function is called as call back function when epin type head search is
// performed
function typeHeadCallBack() {
	if(!$("#documentValuePop").is(":visible")){
		$(".autocomplete").html("").hide();
	}
	var a = $('#procurementEpin').val();
	isValid = isAutoSuggestValid(a, suggestionVal);
	if (!isValid && $('#procurementEpin').val() != ''
			&& $('#procurementEpin').val().length >= 3) {
		$(".autocomplete").html("").hide();
		suggestionVal = "";
		$('#procurementEpin').parent().next().html("! There are no Procurements with this E-PIN");
	} else if(isValid || $('#procurementEpin').val().length < 3){
		$('#procurementEpin').parent().next().html("");
	}
	enableDisableDefaultFilter();
}

// This function is used to view procurement summary for accelerator/agency
function viewProcurementSummary(procurementId) {
	$("#procform").attr("action",
			$("#procform").attr("action") + "&procurementId=" + procurementId);
	$("#submit_action").val("viewProcurement");
	$("#topLevelFromRequest").val("ProcurementRoadmapDetails");
	$("#midLevelFromRequest").val("ProcurementSummary");
	pageGreyOut();
	$("#procform").submit();
};

// This function is used to view procurement summary for provider
function viewProcurementSummaryProvider(procurementId) {
		var isSame = true;
		var tempCheckedCheckBoxes = "";
		$(".favoriteIds:checked").each(function(){
			tempCheckedCheckBoxes = tempCheckedCheckBoxes + $(this).val() + ",";
		});
		if(checkedCheckBoxes != tempCheckedCheckBoxes)
				isSame = false;
		if(!isSame){
			// displays dialogue box when cancel is clicked with unsaved data.
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', modal: true, resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						$("#procform").attr("action",
								$("#procform").attr("action") + "&procurementId=" + procurementId);
						$("#submit_action").val("viewProcurement");
						$("#topLevelFromRequest").val("ProcurementSummaryHeader");
						pageGreyOut();
						$(this).dialog("close");
						$("#procform").submit();
					},
					Cancel: function () {
						$(this).dialog("close");
					}
				},
				close: function (event, ui) {
					$(this).remove();
				}
			});
			$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
		}else{
			$("#procform").attr("action",
					$("#procform").attr("action") + "&procurementId=" + procurementId);
			$("#submit_action").val("viewProcurement");
			$("#topLevelFromRequest").val("ProcurementSummaryHeader");
			pageGreyOut();
			$("#procform").submit();
		}
}

// This will execute when Filter Documents tab is clicked or closed
function setVisibility(id, visibility) {
	if ($("#" + id).is(":visible")) {
		document.procform.reset();
		$(".error").html("");
		enableDisableDefaultParams();
	}
	$("#" + id).toggle();
	callBackInWindow("closePopUp");
}
// This method is used to add new procurement
function addProcurement() {
	pageGreyOut();
	$("#submit_action").val("addNewProcurement");
	$("#topLevelFromRequest").val("ProcurementRoadmapDetails");
	$("#midLevelFromRequest").val("ProcurementSummary");
	$("#procform").submit();
}

// This will execute when Clear Filter button is clicked and will set default
// values for filter
function clearProcurementFilter() {
	var actualDefaultAgency = "All NYC Agencies";
	var programName = "";
	var disableProgram = true;
	var orgType = $("#orgType").val();
	if (orgType == "agency_org") {
		actualDefaultAgency = $("#defaultAgency").val().trim();
		$("#agency").val(actualDefaultAgency);
		disableProgram = false;
		getProgramNameList();
	}
	$("#procurementTitle").val('');
	if (orgType == "city_org" || orgType == "agency_org") {
		$("#procurementEpin").val('');
	}
	$("#agency").val(actualDefaultAgency);
	$("#programName").val(programName);
	$("#service").val('');
	$("#releasedatefrom").val('');
	$("#releasedateto").val('');
	$("#proposalduedatefrom").val('');
	$("#proposalduedateto").val('');
	$("#contractstartdatefrom").val('');
	$("#contractstartdateto").val('');
	$(".selectedSetting").attr('checked', true);
	$(".selectedSetting").attr('disabled', false);
	$(".noSelectSetting").attr('checked', false);
	$(".noSelectSetting").attr('disabled', true);
	if (orgType == "provider_org") {
		$(".providerStatusClass").attr('checked', true);
	}
	$("input[type='text']").each(function() {
		$(this).parent().next().html("");
	});
	$("#clearfilter").attr("disabled", true);
	$("#programName").prop('disabled', disableProgram);
	$("#isOpenEndedRFP").attr('checked', false);
	enableDisabledDates($("#isOpenEndedRFP"));
}

// This will execute when Filter Button tab is clicked and displays filtered
// list
function displayFilter() {
	var epinErrorMessage = $("#procurementEpin").parent().next().text();
	if (null != epinErrorMessage && '' != epinErrorMessage) {
		return false;
	}
	$("input[type='text']").each(function() {
		$(this).parent().next().html("");
	});
	var procurementTitle = $("#procurementTitle").val();
	var isFilterValid = true;
	if (null != procurementTitle && procurementTitle != '') {
		var length = procurementTitle.length;
		if (length < 5) {
			$('#procurementTitle').parent().next().html(
					"! You must enter 5 or more characters");
			return false;
		}
	}
	$("input[type='text']").each(function() {
		if ($(this).attr("validate") == 'calender') {
			isFilterValid = verifyDate(this);
			if (!isFilterValid) {
				return isFilterValid;
			}
		}
	});
	var startDate = '';
	var endDate = '';
	var counter = 1;
	$("input[type='text']")
			.each(
					function(i) {
						if ($(this).attr("startEnd") == 'true') {
							if (counter % 2 == 0) {
								endDate = $(this).val();
								var endDateFinal = new Date(endDate);     
								var startDateFinal= new Date(startDate);
								if (startDate != '' && endDate != ''
										&& (startDateFinal > endDateFinal)) {
									isFilterValid = false;
									$(this)
											.parent()
											.next()
											.html(
													"! The end date must be after the start date");
								}
								++counter;
							} else {
								startDate = $(this).val();
								++counter;
							}
						}
					});
	if (isFilterValid) {
		$("#procform").attr(
				"action",
				$("#procform").attr("action")
						+ "&submit_action=filterProcurement");
		pageGreyOut();
		document.procform.submit();
	}
	
}

// This will execute when Previous,Next.. is clicked for pagination
function paging(pageNumber) {
	var isSame = true;
	var tempCheckedCheckBoxes = "";
	$(".favoriteIds:checked").each(function(){
		tempCheckedCheckBoxes = tempCheckedCheckBoxes + $(this).val() + ",";
	});
	if(checkedCheckBoxes != tempCheckedCheckBoxes)
			isSame = false;
	if(!isSame){
		// displays dialogue box when cancel is clicked with unsaved data.
		$('<div id="dialogBox"></div>').appendTo('body')
		.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
		.dialog({
			modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
			width: 'auto', modal: true, resizable: false, draggable:false,
			dialogClass: 'dialogButtons',
			buttons: {
				OK: function () {
					document.procform.reset();
					$("#procform").attr(
							"action",
							$("#procform").attr("action")
									+ "&submit_action=fetchActiveProcurements&nextPage="
									+ pageNumber);
					pageGreyOut();
					$(this).dialog("close");
					document.procform.submit();
				},
				Cancel: function () {
					$(this).dialog("close");
				}
			},
			close: function (event, ui) {
				$(this).remove();
			}
		});
		$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
	}else{
		document.procform.reset();
		$("#procform").attr(
				"action",
				$("#procform").attr("action")
						+ "&submit_action=fetchActiveProcurements&nextPage="
						+ pageNumber);
		pageGreyOut();
		document.procform.submit();
	}
}

// This will execute when any Column header on grid is clicked for sorting
function sort(columnName) {
	var isSame = true;
	var tempCheckedCheckBoxes = "";
	$(".favoriteIds:checked").each(function(){
		tempCheckedCheckBoxes = tempCheckedCheckBoxes + $(this).val() + ",";
	});
	if(checkedCheckBoxes != tempCheckedCheckBoxes)
			isSame = false;
	if(!isSame){
		// displays dialogue box when cancel is clicked with unsaved data.
		$('<div id="dialogBox"></div>').appendTo('body')
		.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
		.dialog({
			modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
			width: 'auto', modal: true, resizable: false, draggable:false,
			dialogClass: 'dialogButtons',
			buttons: {
				OK: function () {
					document.procform.reset();
					$("#procform")
							.attr(
									"action",
									$("#procform").attr("action")
											+ "&submit_action=sortProcurement&sortGridName=procurementRoadMap"
											+ sortConfig(columnName));
					pageGreyOut();
					$(this).dialog("close");
					document.procform.submit();
				},
				Cancel: function () {
					$(this).dialog("close");
				}
			},
			close: function (event, ui) {
				$(this).remove();
			}
		});
		$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
	}else{
		document.procform.reset();
		$("#procform")
				.attr(
						"action",
						$("#procform").attr("action")
								+ "&submit_action=sortProcurement&sortGridName=procurementRoadMap"
								+ sortConfig(columnName));
		pageGreyOut();
		document.procform.submit();
	}
}

// This will validate epin type ahead search
function isAutoSuggestValid(variableName, suggestionVal) {
	var uoValid = false;
	if (suggestionVal.length > 0) {
		for (i = 0; i < suggestionVal.length; i++) {
			var arrVal = suggestionVal[i].toUpperCase();
			if (arrVal.indexOf(variableName.toUpperCase()) > -1) {
				uoValid = true;
				break;
			}
		}
	}
	return uoValid;
}

/* This function is validates enable and disable of filter button when any form
 field is changed on filter
Updated Method in R4*/
function enableDisableDefaultFilter() {
	var toDisable = true;
	var orgType = $("#orgType").val();
	var procTitle = $("#procurementTitle").val();
	var procEpin = "";
	if (orgType == "city_org") {
		procEpin = $("#procurementEpin").val();
	}
	var agency = $("#agency").val();
	var programName = $("#programName").val();
	var service = $("#service").val();
	var releasedatefrom = $("#releasedatefrom").val();
	var releasedateto = $("#releasedateto").val();
	var proposalduedatefrom = $("#proposalduedatefrom").val();
	var proposalduedateto = $("#proposalduedateto").val();
	var contractstartdatefrom = $("#contractstartdatefrom").val();
	var contractstartdateto = $("#contractstartdateto").val();
	var isFirstLevel = false;
	var isSecondLevel = false;
	var isThirdLevel = false;
	var isFirstLevelCounter = 0;
	var isSecondLevelCounter = 0;
	var isThirdLevelCounter = 0;
	var actualDefaultAgency = "All NYC Agencies";
	if (orgType == "agency_org") {
		actualDefaultAgency = $("#defaultAgency").val();
	}
	$("#firstLevelCheckBox").find('input').each(function() {
		if ($(this).attr("checked") == 'checked') {
			isFirstLevel = true;
			++isFirstLevelCounter;
		}
	});
	$("#secondLevelCheckBox").find('input').each(function() {
		if ($(this).attr("checked") == 'checked') {
			isSecondLevel = true;
			++isSecondLevelCounter;
		}
	});
	if (orgType == "provider_org") {
		$("#thirdLevelCheckBox").find('input').each(function() {
			if ($(this).attr("checked") == 'checked') {
				isThirdLevel = true;
				++isThirdLevelCounter;
			}
		});
		if (procTitle == "" && agency == actualDefaultAgency
				&& programName == "" && service == ""
				&& releasedatefrom == "" && releasedateto == ""
				&& proposalduedatefrom == "" && proposalduedateto == ""
				&& contractstartdatefrom == "" && contractstartdateto == ""
				&& isFirstLevelCounter == 4 && isFirstLevel == true
				&& isSecondLevelCounter == 0 && isSecondLevel == false
				&& isThirdLevelCounter == 9 && isThirdLevel == true
				&& !$("#isOpenEndedRFP").is(":checked")) {
			toDisable = true;
		} else {
			toDisable = false;
		}
	} else {
		if (procTitle == "" && procEpin == "" && agency == actualDefaultAgency
				&& programName == "" && service == ""
				&& releasedatefrom == "" && releasedateto == ""
				&& proposalduedatefrom == "" && proposalduedateto == ""
				&& contractstartdatefrom == "" && contractstartdateto == ""
				&& isFirstLevelCounter == 6 && isFirstLevel == true
				&& isSecondLevelCounter == 0 && isSecondLevel == false
				&& !$("#isOpenEndedRFP").is(":checked")) {
			toDisable = true;
		} else {
			toDisable = false;
		}
	}
	$("#clearfilter").attr("disabled", toDisable);
}

// This funtion fetches program name list depending upon agency selected by user
function getProgramNameList() {
	var url = $("#getProgramListForAgency").val() + "&agencyId="
			+ $("#agency").val()
			+ "&roadMapFilter=filter";
	hhsAjaxRender(null, document.procform,'programNameDiv',url,null);
	$("#programName").prop('disabled', false);
}

// This function validates date fields on filter
function checkForFutureDate(id) {
	var myDate1 = $(id).val();
	var isFilterValid = true;
	if (myDate1 != undefined && myDate1 != '') {
		var month = myDate1.substring(0, 2);
		var date = myDate1.substring(3, 5);
		var year = myDate1.substring(6, 10);
		var currentDate = new Date(year, month - 1, date);
		var today = new Date();
		if (currentDate > today) {
			$(id).parent().next().html("! This date must be in the past");
			isFilterValid = false;
		} else {
			isFilterValid = true;
		}
	}
	return isFilterValid;
}

// This function handles the behaviour of form fields on filters on load of jsp
function enableDisableDefaultParams() {
	var isFiltered = $("#filterItem").val();
	if (isFiltered != "filtered") {
		$(".selectedSetting").attr("checked", true);
		if ($("#orgType").val() == 'provider_org') {
			$(".providerStatusClass").attr("checked", true);
		}
		$("#clearfilter").attr("disabled", true);
	} else {
		$("#clearfilter").attr("disabled", false);
	}
	if ($("#agency").val().trim() != "All NYC Agencies") {
		$("#programName").attr("disabled", false);
	} else {
		$("#programName").attr("disabled", true);
	}
	var isFirstLevel = false;
	var isFirstLevelCounter = 0;
	$("#firstLevelCheckBox").find('input').each(function() {
		if ($(this).attr("checked") == 'checked') {
			isFirstLevel = true;
			++isFirstLevelCounter;
		}
	});
	var isSecondLevel = false;
	var isSecondLevelCounter = 0;
	$("#secondLevelCheckBox").find('input').each(function() {
		if ($(this).attr("checked") == 'checked') {
			isSecondLevel = true;
			++isSecondLevelCounter;
		}
	});
	if ($("#orgType").val() == 'provider_org') {
		if (isFirstLevel && isFirstLevelCounter <= 4) {
			$(".noSelectSetting").attr("disabled", true);
		} else if(isSecondLevel && isSecondLevelCounter <= 2){
			$(".selectedSetting").attr("disabled", true);
		}
	} else {
		if (isFirstLevel && isFirstLevelCounter <= 6) {
			$(".noSelectSetting").attr("disabled", true);
		}
	}
	
	enableDisabledDates($("#isOpenEndedRFP"));
	enableDisableDefaultFilter();
}

/* This method enable disables proposal due dates contract dates
Updated Method in R4*/
function enableDisabledDates(elt){
	if($(elt).is(":checked")){
		$("#proposalduedatefrom, #proposalduedateto, #contractstartdatefrom, #contractstartdateto").val("").attr('disabled', 'disabled');
	}else{
		$("#proposalduedatefrom, #proposalduedateto, #contractstartdatefrom, #contractstartdateto").attr('disabled', false);
	}
}

/* added new method to open calender popup. disables popup if field is disabled
Updated Method in R4*/
function NewCssCalLocal(pCtrl,event,pFormat,pScroller,pShowTime,pTimeMode,pHideSeconds) {
	if(!$("#"+pCtrl).is(":disabled")){
		NewCssCal(pCtrl,event,pFormat,pScroller,pShowTime,pTimeMode,pHideSeconds);
	}
}