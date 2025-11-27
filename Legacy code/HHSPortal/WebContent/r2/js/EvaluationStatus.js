suggestionVal = "";

/**
 *  function used in pagination
 *   Updated Method in R4
 **/
function paging(pageNumber) {
	$("#nextPage").val(pageNumber);
	$("#evaluationForm").attr(
			"action",
			$("#evaluationForm").attr("action")
					+ "&submit_action=pagingEvalution");
	pageGreyOut();
	document.evaluationForm.submit();
}

/**
 *  function used in sorting
 * */
function sort(columnName) {
	pageGreyOut();
	document.evaluationForm.reset();
	$("#evaluationForm")
			.attr(
					"action",
					$("#evaluationForm").attr("action")
							+ "&submit_action=sortEvaluationStatus&sortGridName=proposalEvaluationStatus"
							+ sortConfig(columnName));
	pageGreyOut();
	document.evaluationForm.submit();
}

/** 
 * Cancel Evaluation task button functionality
 * */
function cancelEvalutionTasks(selectElement) {
	pageGreyOut();
	var urlAppender = $("#hiddenCancelEvaluationTasksOverlayContentUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			removePageGreyOut();
			$("#cancelAllEvaluationTasks").html(e);
			$(".overlay").launchOverlay($(".alert-box-cancelEvaluationTasks"),
					$(".cancelAllEvaluationTasks"), "600px", null);
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/** 
 * method will be called when the page will be loaded
 * Updated Method in R4
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
											$("#navigationForm").find(
													"#midLevelFromRequest").eq(
													0).val("EvaluationStatus");
											$("#navigationForm").find("#ES")
													.eq(0).val("0");
											$("#navigationForm").find(
													"#render_action").eq(0)
													.val("getEvaluationStatus");
											$("#navigationForm").find(
													"#evaluationPoolMappingId")
													.eq(0).val($(this).val());
											pageGreyOut();
											document.navigationForm.submit();
										}
									});

					if ($("#screenLockedFlag").val() == "false") {
						var ignoreList = [ "I need to...", "View Proposal",
								"View Evaluation Summary" ];
						$("select[id^=actions] option").filter(function() {
							return $.inArray($(this).text(), ignoreList) < 0;
						}).remove();
						$("select[id^=actions]").attr("disabled", false);
					}// On click of download docs
					$("#DownloadDBDDocs")
							.click(
									function() {
										pageGreyOut();
										var urlAppender = $(
												"#hiddenDownloadDBDDocs").val();
										$
												.ajax({
													type : "POST",
													cache : false,
													url : urlAppender,
													success : function(data) {
														removePageGreyOut();
														var dataJSON = $
																.parseJSON(data);
														if (dataJSON != null
																&& dataJSON.output != null) {
															if (dataJSON.output[0].error != null) {
																$(
																		"#jsMessageContent")
																		.html(
																				dataJSON.output[0].error);
																$(
																		"#jsmessagediv")
																		.show();
															} else {
																var filePath = dataJSON.output[0].path;
																window.location.href = ($(
																		"#contextPathSession")
																		.val()
																		+ "/dbdDoc/" + filePath);
															}
														}
													},
													error : function(data,
															textStatus,
															errorThrown) {
														showErrorMessagePopup();
														removePageGreyOut();
													}
												});
									});

					// Selecting all the checkbox if not coming from filter
					// screen
					if ($("#filtered").val().length == 0) {
						$(':checkbox').prop('checked', true);
					}
					// Type Ahead Functionality for Provider Name
					typeHeadSearch($('#organizationName'), $(
							"#getProviderNameList").val());

					// on click of Mark Returned For Revision button, submit
					// form.
					$("#ProposalForRevision").click(
							function(e) {
								$("#evaluationForm").attr("action",
										$("#markReturnedForRevision").val())
								$("#evaluationForm").submit();
							});

					// on click of do Not Cancel Proposal close overlay
					$("#cancelMarkNonResponsive").click(function(e) {
						overlayLaunched.closeOverlay(e);
					});
					// on click of Cancel for confirmAction overlay
					$("#confirmActionCloseOverlay").click(function(e) {
						overlayLaunched.closeOverlay(e);
					});
					enableDisableDefaultFilter();
				});

/** 
 * Javascript for filter popup
 * */
function setVisibility(id, visibility) {
	$("#" + id).toggle();
	callBackInWindow("closePopUp");
}

/**
 *  Functionality of Set to Default Filter Button
 **/
function clearEvaluationFilter() {
	$("#proposalTitle").val('');
	$("#organizationName").val('');
	$(':checkbox').prop('checked', true);
	enableDisableDefaultFilter();
}

/** 
 * Functionality of Filter button
 **/
function displayFilter() {
	var proposalTitle = $("#proposalTitle").val();
	var isValid = true;
	if (null != proposalTitle && proposalTitle != '') {
		var length = proposalTitle.length;
		if (length < 5) {
			$('#proposalTitle').parent().next().html(
					"! You must enter 5 or more characters");
			return false;
		} else {
			$('#proposalTitle').parent().next().html("");

		}
	}
	$("#evaluationForm").attr(
			"action",
			$("#evaluationForm").attr("action")
					+ "&submit_action=filterEvaluation");
	pageGreyOut();
	document.evaluationForm.submit();
}

/**
 *  this method implements the various functionality on change event of "Actions"
 *    drop down
 *    on the basis of "Selected" value
 *    Updated Method in R4
 * */
function processActionDropDown(proposalId, evaluationPoolMappingId,
		selectElement) {
	var value = $(selectElement).val();
	if (value == "View Proposal") {
		var url = $("#hiddenViewResponse").val()
				+ "&removeMenu=asdas&jspPath=evaluation%2F&proposalId="
				+ proposalId + "&IsProcDocsVisible=" + true;
		window.open(url, 'windowOpenTab',
				'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
	} else if (value == "View Evaluation Summary") {
		var url = $("#hiddenViewEvaluationSummary").val() + "&proposalId="
				+ proposalId + "&evaluationPoolMappingId="
				+ evaluationPoolMappingId;
		window.open(url, 'windowOpenTab',
				'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
	}

	else if (value == "Mark Non-Responsive") {
		$("#proposalId").val(proposalId);
		$(".overlay").launchOverlay($(".alert-box-nonResponsive"),
				$(".cancel-nonResponsive"), "600px", null, "onReady");
	} else if (value == "Mark Returned for Revision") {
		$("#proposalId").val(proposalId);
		$(".overlay").launchOverlay($(".alert-box-returnedForRevision"),
				$(".request-ReturnedForRevision"), "600px", null, "onReady");
	}

	else if (value == "Unlock Proposal") {
		$("#evaluationForm").attr("action",
				$("#hiddenUnlockProposal").val() + "&proposalId=" + proposalId);
		pageGreyOut();
		document.evaluationForm.submit();
	}
	document.getElementById($(selectElement).attr("id")).selectedIndex = "";
}

/** 
 * method to be called on click of send evaluation task button
 *  Updated Method in R4
 **/
function sendEvalutionTasks(selectElement) {
	pageGreyOut();
	var urlAppender = $("#hiddensendEvaluationTasksOverlayContentUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			removePageGreyOut();
			$("#requestCancel").html(e);
			$(".overlay").launchOverlay($(".alert-box-evaluationTasks"),
					$(".cancel-evalutionTasks"), "600px", null);
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/** 
 * this function will implement the functionality on click of proposal hyperlink
 *  Updated Method in R4
 **/
function viewProposalDetail(proposalId) {
	var url = $("#hiddenViewResponse").val()
			+ "&removeMenu=asdas&jspPath=evaluation%2F" + "&IsProcDocsVisible="
			+ true + "&proposalId=" + proposalId;
	window.open(url, 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}
/** 
 * This method clears and closes Overlays
 *  Updated Method in R4
 **/
function clearAndCloseOverLay() {
	$(".overlay").closeOverlay();
}

/** 
 * this function will close the returned for revision overlay
 *  Updated Method in R4
 */
function cancelReturnedForRevisionOverlay() {
	$(".overlay").closeOverlay($(".alert-box-returnedForRevision"),
			$(".exit-panel.returnedForRevision"), "600px", null, "onReady");
}

/** 
 * this method handles the functionality when "Mark Returned For Revision"
 *  option
 *  has been selected on "Actions" drop-down
 *  Updated Method in R4
 */
function returnForRevision() {
	pageGreyOut();
	$("#evaluationForm").attr(
			"action",
			$("#hiddenMarkReturnedForRevision").val() + "&proposalId="
					+ $("#proposalId").val());
	document.evaluationForm.submit();
}

/** 
 * this method handles the functionality when "Mark Non-Responsive" option
 *  has been selected on "Actions" drop-down
 */
function markNonResponsive() {
	pageGreyOut();
	$("#evaluationForm").attr(
			"action",
			$("#hiddenMarkNonResponsive").val() + "&proposalId="
					+ $("#proposalId").val());
	document.evaluationForm.submit();
}

/** 
 *  This function is validates enable and disable of filter button when any form
 *  field is changed on filter
 *  Updated Method in R4
 **/
function enableDisableDefaultFilter() {
	var toDisable = true;
	var orgName = $("#organizationName").val();
	var proposalTitle = $("#proposalTitle").val();

	var isFirstLevel = false;
	var isSecondLevel = false;
	var isFirstLevelCounter = 0;
	var isSecondLevelCounter = 0;
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
	if (orgName == "" && proposalTitle == "" && isFirstLevelCounter == 5
			&& isFirstLevel == true && isSecondLevelCounter == 3
			&& isSecondLevel == true) {
		toDisable = true;
	} else {
		toDisable = false;
	}
	$("#clearfilter").attr("disabled", toDisable);
}


/**
 * Starts R5 : Enhanced Evaluation
 * This function is added to view Progress
 **/
function viewProgress(procurementId, evaluationPoolMappingId){
	pageGreyOut();
	var v_parameter = "procurementId=" + procurementId+"&evaluationPoolMappingId=" + evaluationPoolMappingId;
	var urlAppender = $("#hiddenViewProgressUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			removePageGreyOut();
			$("#viewAllProgress").html(e);
			$(".overlay").launchOverlay($(".alert-box-viewProgress"),
					$(".viewAllProgress"), "950px", null);
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/* Ends R5 : Enhanced Evaluation */