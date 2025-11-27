/**
 * This file contains methods that will handle the
 * Approve Award Task.
 * This file contains method to view procurement summary
 * for agency/provider users also.
 */
var lastDataArray = new Array();
var ischange = false;
/**
 *  loads the page for award approval task
 *  */
$(document)
		.ready(
				function() {
					$('#contractTabs li')
							.removeClass(
									'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
					$("#contractTabs").tabs();
					$("#reassignButton").attr("disabled", "true");
					$("#reassignButton").attr("class", "graybtutton");
					enableDisableFinishButton();
					//R5 Changes starts
					enableDisableNegotiateDropDown();
					if($('#negotiationField').val() == ''){
						$('#negotiateDropDown').val('false');
						$('#negotiateDropDown').hide();
					}
					//R5 changes ends
					$("#saveDiv").hide();
					$("#contractTabs").find('a').each(function() {
						$(this).click(function() {
							if ($(this).hasClass('showButton')) {
								$("#saveDiv").show();
							} else {
								$("#saveDiv").hide();
							}
						});
					});
					$(".evaluationResultDiv tr>th:nth-child(5)").addClass(
							"alignRht nowrap");
					$(".evaluationResultDiv tr>td:nth-child(5)").addClass(
							"alignRht");
					$(".tableAwardAmount").autoNumeric('init', {
						vMax : '9999999999999999',
						vMin : '0.00'
					});
					$("#confirmOverride").attr("disabled", "true");
					$("#confirmOverride").attr("class", "graybtutton");
					$("#validateCheckbox").click(function() {
						if ($("#validateCheckbox").is(':checked')) {
							$("#confirmOverride").removeAttr("disabled");
							$("#confirmOverride").attr("class", "redbtutton");
						} else {
							$("#confirmOverride").attr("disabled", "true");
							$("#confirmOverride").attr("class", "graybtutton");
						}
					});
					// disable all fields if user logged is different from task
					// assigned to
					var screenReadOnly = $("#screenReadOnly").val();
					if (screenReadOnly == 'true') {
						$("#finishButton").attr("class", "graybtutton");
						$("#finishButton").attr("disabled", "true");
						$("#saveButton").attr("class", "graybtutton");
						$("#saveButton").attr("disabled", "true");
						$("#internalComments").attr("disabled", "true");
						$("#finishDropDown").attr("disabled", "true");
					}
					removePageGreyOut();
					$("a[id!='smallA'][id!='mediumA'][id!='largeA']")
							.click(
									function(e) {
										var $self = $(this);
										var isSame = true;
										if (lastDataArray != null
												&& lastDataArray.length > 0) {
											$
													.each(
															lastDataArray,
															function(i) {
																if (!$(
																		lastDataArray[i][1])
																		.compare(
																				$(
																						"form[name='"
																								+ lastDataArray[i][0]
																								+ "']")
																						.serializeArray())) {
																	isSame = false;
																}
															});
										}//displays the dialogue if cancel is clicked without saving
										if (!isSame
												&& lastDataArray != null
												&& lastDataArray.length > 0
												&& !$(this).hasClass(
														"localTabs")) {
											e.preventDefault();
											$('<div id="dialogBox"></div>')
													.appendTo('body')
													.html(
															'<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
													.dialog(
															{
																modal : true,
																title : 'Unsaved Data',
																zIndex : 10000,
																autoOpen : true,
																width : 'auto',
																modal : true,
																resizable : false,
																draggable : false,
																dialogClass : 'dialogButtons',
																buttons : {
																	OK : function() {
																		//Start R5: UX module, clean AutoSave Data
																		deleteAutoSaveData();
																		//End R5: UX module, clean AutoSave Data
																		document.location = $self
																				.attr('href');
																		$(this)
																				.dialog(
																						"close");
																	},
																	Cancel : function() {
																		$(this)
																				.dialog(
																						"close");
																	}
																},
																close : function(
																		event,
																		ui) {
																	$(this)
																			.remove();
																}
															});
											$(
													"div.dialogButtons div button:nth-child(2)")
													.find("span").addClass(
															"graybtutton");
										}
									});
				});

/**
 *  This function is used to view procurement summary for agency/provider users
 *  */
function viewProcurementSummary() {
	var url = $("#procurementSummaryURL").val()
			+ "&action=procurementHandler&overlay=true";
	window.open(url, 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

/**
 *  This function is used to save internal comments
*/
function saveApproveAwardTaskDetails() {
	if (validateTextArea("internalComments")) {
		$("#approveAwardForm").attr(
				"action",
				$("#approveAwardForm").attr("action")
						+ "&submit_action=saveApproveAwardTaskDetails");
		var ignoreForms = [];
		updateSavedData(ignoreForms);
		pageGreyOut();
		document.approveAwardForm.submit();
	} else {
		removePageGreyOut();
		$("#ErrorDiv").html(invalidResponseMsg);
		$("#ErrorDiv").show();
	}
}

/**
 *  This function is used to view proposal summary for selected proposal Id
 * */
function viewProposalDetail(proposalId) {
	var url = $("#viewProposalSummaryUrl").val() + "&proposalId=" + proposalId
			+ "&jspPath=evaluation/&removeMenu=asdas&fromAwardTask=true";
	window.open(url, 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

/**
 *  This function is used to view evaluation summary for selected proposal Id
*	Updated Method in R4
**/
function viewEvaluationSummary(proposalId) {
	window.open($("#viewEvaluationSummaryUrl").val() + "&proposalId="
			+ escape(proposalId) + "&procurementId="
			+ $("#procurementId").val() + "&evaluationPoolMappingId="
			+ $("#evaluationPoolMappingId").val() +"&removeMenu=asdas", 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

/**
 *  This function is used to view comments for selected proposal Id
*/
function viewAccComments(proposalId) {
	var url = $("#viewSelectionComments").val() + "&proposalId=" + proposalId;
	var jqxhr = $.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(data) {
			$(".overlay").launchOverlay($(".alert-box-viewComments"),
					$(".exit-panel.upload-exit"), "600px", null, "onReady");
			var dataJSON = $.parseJSON(data);
			if (dataJSON.selectionComments != null) {
				$(".commentclass1").html(
						dataJSON.selectionComments[0].ProviderName);
				$(".commentclass2").html(
						dataJSON.selectionComments[0].ProposalTitle);
				$(".commentclass3")
						.html(dataJSON.selectionComments[0].Comments);
			}
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 *  This function is used to enable disable reassign button
 **/
function enableDisableReassignButton() {
	var selectedVal = $("#reassignDropDown").val();
	if (null == selectedVal || "" == selectedVal) {
		$("#reassignButton").attr("class", "graybtutton");
		$("#reassignButton").attr("disabled", "true");
	} else {
		$("#reassignButton").attr("class", "button");
		$("#reassignButton").removeAttr("disabled");
	}
}

/**
 *  This function is used to reassign task to selected user
 * */
function reassignTask() {
	pageGreyOut();
	var reAssignUser = $('#reassignDropDown option:selected').html();
	$("#reassignedToUserName").val(reAssignUser);
	$("#approveAwardForm").attr(
			"action",
			$("#approveAwardForm").attr("action")
					+ "&submit_action=reassignAwardApprovalTask");
	pageGreyOut();
	document.approveAwardForm.submit();
}

/**
 *  This function is used to enable disable finish button
 * */
function enableDisableFinishButton() {
	// Start Updated in R5
	var selectedVal = $("#negotiateDropDown").val();
	// End Updated in R5
	if (null == selectedVal || "" == selectedVal) {
		$("#finishButton").attr("class", "graybtutton");
		$("#finishButton").attr("disabled", "true");
	} else {
		$("#finishButton").attr("class", "button");
		$("#finishButton").removeAttr("disabled");
	}
}
/**
 *  R5 Change starts
*	This function is used to enable disable Negotiate Drop Down
**/
function enableDisableNegotiateDropDown() {
	var selectedVal = $("#finishDropDown").val();
	if($('#negotiateDropDown').is(':visible'))
	{
		if (null == selectedVal || "" == selectedVal) {
			$("#negotiateDropDown").attr("disabled", "true");
			$('#negotiateDropDown').val("");
			$("#finishButton").attr("class", "graybtutton");
			$("#finishButton").attr("disabled", "true");
		} else if(null == selectedVal || selectedVal == "48")
		{
			$("#finishButton").attr("class", "button");
			$("#finishButton").removeAttr("disabled");
			$("#negotiateDropDown").attr("disabled", "true");
			$('#negotiateDropDown').val("");
		}
		else {
			$("#negotiateDropDown").removeAttr("disabled");
			$("#finishButton").attr("class", "graybtutton");
			$("#finishButton").attr("disabled", "true");
			$('#negotiateDropDown').val("");
		}
	}
	else
		{
		if(null == selectedVal || "" == selectedVal){
			$("#finishButton").attr("class", "graybtutton");
			$("#finishButton").attr("disabled", "true");
			}
		else if(null == selectedVal || selectedVal == "48"){
			$("#finishButton").attr("class", "button");
			$("#finishButton").removeAttr("disabled");
			}
		else{
			$("#finishButton").attr("class", "button");
			$("#finishButton").removeAttr("disabled");
			}
		}
	
}
/**
 *  R5 Change ends
 *	This function is used to reassign task to selected user
 **/
function finishTask() {
	var selectedVal = $("#finishDropDown").val();
	var internalComments = $("#internalComments").val();
	if (selectedVal == '49') 
	{
		//Start QC 9674 R 9.5 
		/*
		pageGreyOut();
		$(".overlay").launchOverlay($(".alert-box-confirmOverride"),
				$(".exit-panel.upload-exit"), "600px", null);
		// $.unblockUI();
		removePageGreyOut();
		*/
				
		confirmOverride();
		
		//End QC 9674 R 9.5
		
	} else if (selectedVal == '48'
			&& (internalComments == null || internalComments == "")) {
		$("#messagediv").text("Comments are required when returning awards.");
		$("#messagediv").show();
	} else {
		if (validateTextArea("internalComments")) {
			pageGreyOut();
			$("#approveAwardForm").attr(
					"action",
					$("#approveAwardForm").attr("action")
							+ "&submit_action=finishAwardApprovalTask");
			document.approveAwardForm.submit();
		} else {
			removePageGreyOut();
			$("#ErrorDiv").html(invalidResponseMsg);
			$("#ErrorDiv").show();
		}
		
	}
}

/**
 *  This function is used to close overlay
*/
function cancelOverLay() {
	$(".overlay").closeOverlay();
}

/**
 * This function is used to approve Award and return to task inbox
*/
function confirmOverride() {
	$("#submit_action").val("confirmOverride");
	
	var options = {
		success : function(responseText, statusText, xhr) {
			removePageGreyOut();
			window.location.href = $("#approveAwardRenderUrl").val()
					+ "&returninbox=inbox" + '&taskUnlock='
					+ $("#workflowId").val();
		},
		error : function(xhr, ajaxOptions, thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	pageGreyOut();
	$("#approveAwardForm").ajaxSubmit(options); //uncomment after test
	
}

/**
 *  This function is used to return to task inbox or task management screen from
 *	which the user comes in
 **/
function returnToTaskInbox(taskId) {
	$("#approveAwardForm").attr(
			"action",
			$("#approveAwardRenderUrl").val() + "&returninbox=inbox"
					+ '&taskUnlock=' + taskId);
	document.approveAwardForm.submit();
}

$(window).load(function() {
	var ignoreForms = [];
	updateSavedData(ignoreForms);
});
