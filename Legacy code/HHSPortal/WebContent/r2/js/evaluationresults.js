/**
 * This file handle events of 
 * Evalutaion Summary Action
 */
groupClass = "selectedSetting";
otherClass = "noSelectSetting";
groupCSS = "." + groupClass;
otherCSS = "." + otherClass;
suggestionVal = "";
var isvalid = false;
/**
 *  function to be called when the page will be loaded.
 *  Updated Method in R4
 **/
$(document)
		.ready(
				function() {
					// on click of return to evaluation summary button
					$("#returnEvaluationSummary")
							.click(
									function() {
										$("#navigationForm").find("#forAction")
												.eq(0).val("propEval");
										$("#navigationForm").find("#ES").eq(0)
												.val("0");
										$("#navigationForm").find(
												"#topLevelFromRequest").eq(0)
												.val("ProposalsandEvaluations");
										pageGreyOut();
										document.navigationForm.submit();
									});
					// on changing competition pool from drop down
					$("#compPoolDropDown")
							.change(
									function() {
										if ($(this).val() != -1
												&& $(this).val() != $(
														"#navigationForm")
														.find(
																"#evaluationPoolMappingId")
														.eq(0).val()) {
											$("#navigationForm").find(
													"#forAction").eq(0).val(
													"propEval");
											$("#navigationForm").find(
													"#topLevelFromRequest").eq(
													0).val(
													"ProposalsandEvaluations");
											$("#navigationForm")
													.find(
															"#midLevelFromRequest")
													.eq(0)
													.val(
															"EvaluationResultsandSelections");
											$("#navigationForm").find("#ES")
													.eq(0).val("0");
											$("#navigationForm")
													.find("#render_action")
													.eq(0)
													.val(
															"fetchEvaluationResults");
											$("#navigationForm").find(
													"#evaluationPoolMappingId")
													.eq(0).val($(this).val());
											pageGreyOut();
											document.navigationForm.submit();
										}
									});
					if ($("#screenLockedFlag").val() == "false") {
						var ignoreList = [ "I need to...", "View Proposal",
								"View Evaluation Summary", "View Comments" ];
						$("select[id^=actions] option").filter(function() {
							return $.inArray($(this).text(), ignoreList) < 0;
						}).remove();
						$("select[id^=actions]").attr("disabled", false);
					}
					$('#awardAmountFrom').autoNumeric('init', {
						vMax : '9999999999999999',
						vMin : '0.00'
					});
					$('#awardAmountTo').autoNumeric('init', {
						vMax : '9999999999999999',
						vMin : '0.00'
					});
					$("#evalScoreId").each(function() {
						$(this).html(parseFloat($(this).html()).toFixed(2));
					});
					// typehead for organisation name
					typeHeadSearch($('#organizationName'), $(
							"#getProviderNameList").val());
					if ($("#proposalFilteredResult").val().length == 0) {
						$("#proposalTitle").val('');
						$("#organizationName").val('');
						$("#scoreRangeFrom").val('');
						$("#scoreRangeTo").val('');
						$("#awardAmountFrom").val('');
						$("#awardAmountTo").val('');
						$(':checkbox').prop('checked', true);
					}
					;

					$(".evaluationResultDiv tr>td:nth-child(4)").each(
							function() {
								$(this).html(
										parseFloat($(this).html()).toFixed(2));
							});
					$(".evaluationResultDiv tr>th:nth-child(6)").addClass(
							"alignRht nowrap");
					$(".evaluationResultDiv tr>td:nth-child(6)").addClass(
							"alignRht");

					$(".tableAwardAmount").each(function(e) {
						$(this).autoNumeric('init', {
							vMax : '9999999999999999',
							vMin : '0.00'
						});
						$(this).attr("style", "text-align:center");
					});
					if ($("#userRole").val().indexOf("FINANCE_") == 0
							|| $("#userRole").val().indexOf("PROGRAM_") == 0
							|| $("#userRole").val().indexOf("CFO") == 0) {
						$('select[id^="actions"]').attr("disabled", true);
					}
					enableDisableDefaultFilter();
					//Changes in R5 starts
					$('#pendingAwardTipDiv').hide();
					if($(".red-ex-mark").size()>1)
						{
						$('#pendingAwardTipDiv').show();
						$('.tabularWrapper table').find('.tableAwardAmount').css({'float':'none'});
						}
					//Changes in R5 ends
					
				});
/**
 *  to be called when user type in the provider name field
 **/
function typeHeadCallBack() {
	if (!isvalid && $('#procurementEpin').val() != ''
			&& $('#procurementEpin').val().length > 3) {
		$(".autocomplete").html("").hide();
		suggestionVal = "";
		$('#procurementEpin').parent().next().html(
				"! There are no Procurements with this E-PIN");
	} else if ($('#procurementEpin').val().length <= 3) {
		$('#procurementEpin').parent().next().html("");
	}
	enableDisableDefaultFilter();
}

/**
 *  This will execute when Filter Documents tab is clicked or closed
 *  */
function setVisibility(id, visibility) {
	$("#" + id).toggle();
	callBackInWindow("closePopUp");
}
/**
 *  This method is used to add new procurement
*/
function addProcurement() {
	var url = $("#procform").attr("action")
			+ "&submit_action=addNewProcurement&topLevelFromRequest=ProcurementRoadmapDetails&midLevelFromRequest=ProcurementSummary";
	window.location.href = url;
}

/**
 *  This will execute when Clear Filter button is clicked and will set default
 *   values for filter
 *   Functionality of Set to Default Filter Button
 */
function clearEvaluationFilter() {
	$('#proposalTitle').parent().next().html("");
	$('#scoreRangeFrom').parent().parent().next().html("");
	$('#scoreRangeTo').parent().parent().next().html("");
	$('#awardAmountTo').parent().parent().next().html("");
	$('#awardAmountFrom').parent().parent().next().html("");
	$("#proposalTitle").val("");
	$("#organizationName").val("");
	$("#scoreRangeFrom").val('');
	$("#scoreRangeTo").val('');
	$("#awardAmountFrom").val('');
	$("#awardAmountTo").val('');
	$(':checkbox').prop('checked', true);

}
/**
 *  This will execute when Filter Button tab is clicked and displays filtered
 * list
 */
function displayFilter() {
	$(".error").html("");
	var proposalTitle = $("#proposalTitle").val();
	var scoreRangeFrom = $("#scoreRangeFrom").val();
	var scoreRangeTo = $("#scoreRangeTo").val();
	var awardAmountFrom = $("#awardAmountFrom").val();
	var awardAmountTo = $("#awardAmountTo").val();

	var isValid = true;
	// if proposal title is not null and non empty
	if (null != proposalTitle && proposalTitle != '') {
		var length = proposalTitle.length;
		if (length < 5) {
			$('#proposalTitle').parent().next().html(
					"! You must enter 5 or more characters");
			isValid = false;
		}
	}
	// if scoreRangeFrom is not null and non empty
	if (null != scoreRangeFrom && scoreRangeFrom != '') {
		if (scoreRangeFrom > 100 || scoreRangeFrom < 0) {
			$('#scoreRangeFrom').parent().parent().next().html(
					"! Values must be between 0 and 100.");
			isValid = false;
		} else if (null == scoreRangeTo || scoreRangeTo == '') {
			scoreRangeTo = 100;
			$("#scoreToHidden").val("100");
		}
	}
	// if scoreRangeTo is less than scoreRangeFrom
	if ((scoreRangeTo - scoreRangeFrom) < 0) {
		$('#scoreRangeTo').parent().parent().next().html(
				"! Score Range to must be larger than Score Range from");
		isValid = false;
	}
	// if scoreRangeFrom is not null and non empty
	if (null != scoreRangeFrom && scoreRangeFrom != '') {
		if (scoreRangeFrom > 100 || scoreRangeFrom < 0) {
			$('#scoreRangeFrom').parent().parent().next().html(
					"! Values must be between 0 and 100.");
			isValid = false;
		} else if (null == scoreRangeTo || scoreRangeTo == '') {
			scoreRangeTo = 100;
			$("#scoreToHidden").val("100");
		}
	}
	// if scoreRangeTo is not null and non empty
	if (null != scoreRangeTo && scoreRangeTo != '') {
		if (scoreRangeTo > 100 || scoreRangeTo < 0) {
			$('#scoreRangeTo').parent().parent().next().html(
					"! Values must be between 0 and 100.");
			isValid = false;
		} else if (null == scoreRangeFrom || scoreRangeFrom == '') {
			scoreRangeFrom = 0;
			$("#scoreFromHidden").val("0");
		}
	}

	// if awardAmountFrom is not null and non empty
	if (null != awardAmountFrom && awardAmountFrom != '') {
		awardAmountFrom = awardAmountFrom.replaceAll(",", "");
		if (awardAmountFrom < 0) {
			$('#awardAmountFrom').parent().parent().next().html(
					"!Value should be numerical greater than 0");
			isValid = false;
		} else if (null == awardAmountTo || awardAmountTo == '') {
			awardAmountTo = 9999999999999999.99;
			$("#awardAmountToHidden").val("9999999999999999.99");
		}
	}
	// if awardAmountTo is less than awardAmountFrom
	if ((awardAmountTo - awardAmountFrom) < 0) {
		$('#awardAmountTo').parent().parent().next().html(
				"! Award Amount to must be larger than Award Amount from");
		isValid = false;
	}

	// if awardAmountTo is not null and non empty
	if (null != awardAmountTo && awardAmountTo != '') {
		if (awardAmountTo != 9999999999999999.99) {
			awardAmountTo = awardAmountTo.replaceAll(",", "");
		}
		if (awardAmountTo < 0) {
			$('#awardAmountTo').parent().parent().next().html(
					"!Value should be numerical greater than 0");
			isValid = false;
		} else if (null == awardAmountFrom || awardAmountFrom == '') {
			awardAmountFrom = 0;
			$("#awardAmountFromHidden").val("0");
		}
	}
	// if awardAmountFrom is not null and non empty
	if (null != awardAmountFrom && awardAmountFrom != '') {
		awardAmountFrom = awardAmountFrom.replaceAll(",", "");
		if (awardAmountFrom < 0) {
			$('#awardAmountFrom').parent().parent().next().html(
					"!Value should be numerical greater than 0");
			isValid = false;
		} else if (null == awardAmountTo || awardAmountTo == '') {
			awardAmountTo = 9999999999999999.99;
			$("#awardAmountToHidden").val("9999999999999999.99");
		}
	}
	if (isValid) {
		$('#proposalTitle').parent().next().html("");
		$('#scoreRangeFrom').parent().parent().next().html("");
		$('#scoreRangeTo').parent().parent().next().html("");
		$('#awardAmountTo').parent().parent().next().html("");
		$('#awardAmountFrom').parent().parent().next().html("");
		if ($("#awardAmountFrom").val() != '') {
			$("#awardAmountFrom").val(
					$("#awardAmountFrom").val().replaceAll(",", ""));
		}
		if ($("#awardAmountTo").val() != '') {
			$("#awardAmountTo").val(
					$("#awardAmountTo").val().replaceAll(",", ""));
		}
		$("#evalResultform").attr(
				"action",
				$("#evalResultform").attr("action")
						+ "&submit_action=filterEvaluationResults");
		pageGreyOut();
		document.evalResultform.submit();
	}

}

/**
 *  This will execute when Previous,Next.. is clicked for pagination
 **/
function paging(pageNumber) {
	$("#evalResultform").attr(
			"action",
			$("#evalResultform").attr("action")
					+ "&submit_action=pagingEvalutionResults&nextPage="
					+ pageNumber);
	pageGreyOut();
	document.evalResultform.submit();

}

/**
*  This will execute when any Column header on grid is clicked for sorting
* function used in sorting
*/
function sort(columnName) {
	pageGreyOut();
	document.evalResultform.reset();
	$("#evalResultform")
			.attr(
					"action",
					$("#evalResultform").attr("action")
							+ "&submit_action=sortEvaluationResults&sortGridName=proposalEvaluationResults"
							+ sortConfig(columnName));
	pageGreyOut();
	document.evalResultform.submit();
}

/**
 *  This will validate epin type ahead search
*/
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

/**
 *  to get the program name list on change of agency
*/
function getProgramNameList() {
	var url = $("#getProgramListForAgency").val() + "&agencyId="
			+ $("#agency").val();
	var jqxhr = $.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(data) {
			$("#programName").html(data);
			$("#programName").prop('disabled', false);
		},
		error : function(data, textStatus, errorThrown) {
		}
	});
}
/**
 *  to be called when user selects an action from the dropdown
 **/
function processAction(proposalId, selectElement) {
	var value = $(selectElement).val();
	// if selected value is "View Proposal"
	if (value == "View Proposal") {
		var url = $("#hiddenViewResponse").val()
				+ "&removeMenu=asdas&jspPath=evaluation%2F&proposalId="
				+ proposalId + "&IsProcDocsVisible=" + true;
		window.open(url, 'windowOpenTab',
				'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
	}
	// if selected value is "View Evaluation Summary"
	else if (value == "View Evaluation Summary") {
		var url = $("#hiddenViewEvaluationSummary").val() + "&proposalId="
				+ proposalId;
		window.open(url, 'windowOpenTab',
				'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
	}
	// if selected value is "Mark Selected"
	else if (value == "Mark Selected") {
		pageGreyOut();
		var urlAppender = $("#hiddenMarkSelected").val() + "&proposalId="
				+ proposalId;
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			success : function(e) {
				removePageGreyOut();
				$("#requestMarkSelected").html(e);
				$(".overlay").launchOverlay($(".alert-box-markSelected"),
						$(".mark-Selected"), "600px", null);
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
	// if selected value is "Mark Not Selected"
	else if (value == "Mark Not Selected") {
		pageGreyOut();
		var urlAppender = $("#hiddenMarkNotSelected").val() + "&proposalId="
				+ proposalId;
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			success : function(e) {
				removePageGreyOut();
				$("#requestMarkNotSelected").html(e);
				$(".overlay").launchOverlay($(".alert-box-markNotSelected"),
						$(".mark-NotSelected"), "600px", null);
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
	// if selected value is "View Comments"
	else if (value == "View Comments") {
		pageGreyOut();
		var urlAppender = $("#hiddenViewComments").val() + "&proposalId="
				+ proposalId;
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			success : function(e) {
				removePageGreyOut();
				$("#requestViewComments").html(e);
				$(".overlay").launchOverlay($(".alert-box-viewComments"),
						$(".view-Comments"), "600px", null);
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
	// if selected value is "Request Score Amendment"
	else if (value == "Request Score Amendment") {
		$("#proposalId").val(proposalId);
		$(".overlay").launchOverlay($(".alert-box-requestScoreAmendment"),
				$(".request-ScoreAmendment"), "600px", null, "onReady");
	}
	document.getElementById($(selectElement).attr("id")).selectedIndex = "";
}

/**
 *  this function will close the request score amendment overlay
 **/
function cancelRequestAmendmentOverlay() {
	$(".overlay").closeOverlay($(".alert-box-requestScoreAmendment"),
			$(".exit-panel.request-ScoreAmendment"), "600px", null, "onReady");
}

/**
*  this function will implement functionality on click of request score
* amendment button
*/
function sendAmendmentRequest() {
	pageGreyOut();
	window.location.href = $("#sendRequestAmendment").val() + "&proposalId="
			+ $("#proposalId").val();
}

/**
 *  this function will implement the functionality on click of proposal hyperlink
*/
function viewProposalDetail(proposalId) {
	var url = $("#hiddenViewResponse").val()
			+ "&removeMenu=asdas&jspPath=evaluation%2F&proposalId="
			+ proposalId + "&IsProcDocsVisible=" + true;
	window.open(url, 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

/**
 *  Review Award Comments are being displayed on click of "Show Comments"
 *  hyperlink
 *  which are being handled by showComments() method
 *  */
function showComments() {
	pageGreyOut();
	var urlAppender = $("#hiddenViewAwardComments").val() + "&procurementId="
			+ $("#procurementId").val();
	
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			removePageGreyOut();
			$("#requestAwardComments").html(e);
			$(".overlay").launchOverlay($(".alert-box-viewAwardComments"),
					$(".request-AwardComments"), "600px", null);
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 *  Function to be called when user clicks on the update or finalize button
 *  */
function finalizeOrUpdateResults(selectElement, nextAction) {
	pageGreyOut();
	var urlAppender = $("#hiddenFinalizeOrUpdateResultsOverlayContentUrl")
			.val()
			+ "&nextAction=" + nextAction;
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			removePageGreyOut();
			$("#finalizeOrUpdateResults").html(e);
			$(".overlay").launchOverlay(
					$(".alert-box-finalizeOrUpdateResults"),
					$(".cancel-finalizeOrUpdateResults"), "600px", null);
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 *  This function is validates enable and disable of filter button when any form
 *	field is changed on filter
 **/
function enableDisableDefaultFilter() {
	var toDisable = true;
	var orgName = $("#organizationName").val();
	var proposalTitle = $("#proposalTitle").val();
	var scoreRangeFrom = $("#scoreRangeFrom").val();
	var scoreRangeTo = $("#scoreRangeTo").val();
	var awardAmountFrom = $("#awardAmountFrom").val();
	var awardAmountTo = $("#awardAmountTo").val();

	var isFirstLevel = false;
	var isFirstLevelCounter = 0;
	$("#firstLevelCheckBox").find('input').each(function() {
		if ($(this).attr("checked") == 'checked') {
			isFirstLevel = true;
			++isFirstLevelCounter;
		}
	});
	if (orgName == "" && proposalTitle == "" && scoreRangeFrom == ""
			&& scoreRangeTo == "" && awardAmountFrom == ""
			&& awardAmountTo == "" && isFirstLevelCounter == 4
			&& isFirstLevel == true) {
		toDisable = true;
	} else {
		toDisable = false;
	}
	$("#clearfilter").attr("disabled", toDisable);
}
