/**
 * This Js file has functions of Finalize Award Amount module
 **/
var lastDataArray = new Array();
var ischange = false;
/**
 *  loads the page for finalize amount task
 **/
$(document)
		.ready(
				function() {
					$('#contractTabs li')
							.removeClass(
									'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
					$("#contractTabs").tabs();
					$("#reassignButton").attr("disabled", "true");
					$("#reassignButton").attr("class", "graybtutton");
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
					$(".evaluationResultDiv tr>th:nth-child(5), .evaluationResultDiv tr>th:nth-child(6)").addClass(
					"alignRht nowrap");
					$(".evaluationResultDiv tr>td:nth-child(5), .evaluationResultDiv tr>td:nth-child(6)").addClass(
					"alignRht");
					updateEstTotalAmount();
					$(".tableAwardAmount").autoNumeric('init', {
						vMax : '9999999999999999',
						vMin : '0.00'
					});
					// disable all fields if user logged is different from task
					// assigned to
					var screenReadOnly = $("#screenReadOnly").val();
					if (screenReadOnly == 'true') {
						$("#finishButton").attr("class", "graybtutton");
						$("#finishButton").attr("disabled", "true");
						$("#finishtaskchild").attr("disabled", "true");
						$("#saveButton").attr("class", "graybtutton");
						$("#saveButton").attr("disabled", "true");
						$("#internalComments").attr("disabled", "true");
						$("#cancelButton").attr("class", "graybtutton");
						$("#cancelButton").attr("disabled", "true");
					}
					else
					{
						$("#finishButton").attr("disabled", "true");
						$("#finishButton").attr("class", "button");
						$("#cancelButton").attr("class", "button");
						$("#cancelButton").removeAttr("disabled");						
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
					$("#inputAwardAmountTableDiv table").append('<tr class="sumRow"><td colspan=3></td><td><b>Total: </b></td><td id="totalEstAmt" class="tableAwardAmount" ><label></label></td><td id="totalFinalAmt" class="tableAwardAmount"><label></label></td></tr>');
					updateFinalTotalAmount();
					updateEstTotalAmount();
				});

/**
 * This function is used to view procurement summary for agency/provider users
 **/
function viewProcurementSummary() {
	var url = $("#procurementSummaryURL").val()
			+ "&action=procurementHandler&overlay=true";
	window.open(url, 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

/**
 * This function is used to save internal comments
 **/
function saveFinalizeAwardComments() {
	if (validateTextArea("internalComments")) {
		pageGreyOut();
		document.approveAwardAmountForm.action = $("#saveApproveAwardComments").val();
		var options = {
	   		//function finds the elements belonging to class overlaycontent
				success : function(responseText, statusText, xhr) {
					removePageGreyOut();
					// Defect 7397 changes
					var previousComment = $('#internalComments').val();
					$('#previousComments').val(previousComment);
					// Defect 7397 changes
					var ignoreForms = [];
					updateSavedData(ignoreForms);
				},//this function calls overlaycontent function
				error : function(xhr, ajaxOptions, thrownError) {
					showErrorMessagePopup();
					removePageGreyOut();
				}
			};
	  $(document.approveAwardAmountForm).ajaxSubmit(options); 
	} else {
		removePageGreyOut();
		$("#ErrorDiv").html(invalidResponseMsg);
		$("#ErrorDiv").show();
	}
}
/**
 * This function is used to view proposal summary for selected proposal Id
 **/
function viewProposalDetail(proposalId) {
	var url = $("#viewProposalSummaryUrl").val() + "&proposalId=" + proposalId
			+ "&jspPath=evaluation/&removeMenu=asdas&fromAwardTask=true";
	window.open(url, 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

/**
 *  This function is used to view proposal summary for selected proposal Id
 **/
function viewEditAmmount(proposalId) {
	var url = $("#viewProposalSummaryUrl").val() + "&proposalId=" + proposalId
			+ "&jspPath=evaluation/&removeMenu=asdas&fromAwardTask=true";
	window.open(url, 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

/**
 * This function is used to view evaluation summary for selected proposal Id
 * Updated Method in R4
 **/
function viewEvaluationSummary(proposalId) {
	window.open($("#viewEvaluationSummaryUrl").val() + "&proposalId="
			+ escape(proposalId) + "&procurementId="
			+ $("#procurementId").val() + "&evaluationPoolMappingId="
			+ $("#evaluationPoolMappingId").val() +"&removeMenu=asdas", 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

/**
 * This function is used to enable disable reassign button
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
 * Used to enable disable finish Button
 **/
function enableFinishButton()
{
	if(document.getElementById("finishtaskchild").value ==''){
		$("#finishButton").attr("disabled", "true");
	}
	else
	{
		$("#finishButton").removeAttr("disabled");
	}
}
/**
 * This function is used to reassign task to selected user
 **/
function reassignTask() {
	pageGreyOut();
	var reAssignUser = $('#reassignDropDown option:selected').html();
	$("#reassignedToUserName").val(reAssignUser);
	$("#approveAwardAmountForm").attr(
			"action",
			$("#reassignApproveAwardAmountUrl").val()
					+ "&submit_action=reassignApproveAwardAmountTask");
	pageGreyOut();
	document.approveAwardAmountForm.submit();
}
/**
 * This function is used to check if Comment text area is Blank
 **/
function isCommentBlank(){
	var _isBlank = false;
	if($.trim($("#internalComments").val()) == ""){
		_isBlank = true;
	}
	return _isBlank;
}
/**
 *  This function is used to reassign task to selected user
 **/
function finishTask() {
	if ($("#finishtaskchild").val() == "Returned") {
		if (isCommentBlank()) {
			$('#ErrorDiv').html("Comments are required when returning awards.");
			$('#ErrorDiv').show();
			
			 $("#approveAwardAmountForm").submit(function(e){
			        e.preventDefault();
			    });
		} else {
			$("#approveAwardAmountForm").attr("action", $("#returnApproveAwardAmountTask").val());
			document.approveAwardAmountForm.submit();
			 $('#finishButton').prop('disabled', true);
		}
	}
 	else if ($("#finishtaskchild").val() == "Approved") {
		$("#approveAwardAmountForm").attr("action",
				$("#finishApproveAwardAmountTask").val());
		document.approveAwardAmountForm.submit();
		 $('#finishButton').prop('disabled', true);
	}
}

/** 
 * This function is used to return to task inbox or task management screen from
 *  which the user comes in
 **/
function returnToTaskInbox(taskId) {
	$("#approveAwardAmountForm").attr(
			"action",
			$("#approveAwardRenderUrl").val() + "&returninbox=inbox"
					+ '&taskUnlock=' + taskId);
	document.approveAwardAmountForm.submit();
}

$(window).load(function() {
	var ignoreForms = [];
	updateSavedData(ignoreForms);
});
/**
 * This function is executed to update final total amount
 **/
function updateFinalTotalAmount(){
	var sum = new Big(0);
	$(".evaluationResultDiv table tr").each(function(){
		if($(this).find("td").size() == 6 && $(this).find("td").eq(5).text() != 'Edit Amount'){
			sum = sum.plus(new Big($(this).find("td").eq(5).text().replaceAll(",", "").trim()).toFixed(2));
		}
	});
	if(sum == '0'){
		sum='--';
	}
	$("#totalFinalAmt label").text(sum);
	if(sum != '0'){
		$("#totalFinalAmt label").autoNumeric("destroy");
		$("#totalFinalAmt label").autoNumeric({
			vMax : '9999999999999999',
			vMin : '0.00'
		});
	}
}
/**
 * This function is executed to update estimated total amount
 **/
function updateEstTotalAmount(){
	var sum = new Big(0);
	$(".evaluationResultDiv table tr").each(function(){
		if($(this).find("td").size() == 6){
			sum = sum.plus(new Big($(this).find("td").eq(4).text().replaceAll(",", "").trim()).toFixed(2));
		}
	});
	$("#totalEstAmt label").text(sum);
	$("#totalEstAmt label").autoNumeric("destroy");
	$("#totalEstAmt label").autoNumeric( {
		vMax : '9999999999999999',
		vMin : '0.00'
	});
}