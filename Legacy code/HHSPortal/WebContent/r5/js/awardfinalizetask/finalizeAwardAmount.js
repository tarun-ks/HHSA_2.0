/**
 * This Js file has functions that will handle 
 * events of Finalize award amount module
 **/
var lastDataArray = new Array();
var ischange = false;
/**
 *  loads the page for finalize amount task
 **/
$(document)
		.ready(
				function() {
					$("#finalizeAwardForm")
					.validate(
							{
								rules : {
									AMOUNT : {
										required : true,
										maxStrict : 9999999999999999.99,
										maxlength : 18
									}
								},
								messages : {
									AMOUNT : {
										required : "! This field is required",
										maxStrict : "! Please enter a value less than $10,000,000,000,000,000.00",
										maxlength : "! Input should be less than or equal to 18 digits"
									}
								},
								submitHandler : function(form) {
									confirmAmount();									
								},
								errorPlacement : function(error, element) {
									error.appendTo(element.parent().parent().find("span.error"));
									if(BrowserDetect.browser == 'Explorer' && BrowserDetect.version==7){
										var errorObject = $(".row").find("span >label.error");
										$(errorObject).each(function(){
											if($(this).html()!='' && $(this).html()!='*'){
												$(this).parent().parent().parent().removeAttr("style");
											}
										});
									}
								}
							});
					
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
					$('#finalAmount').autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
					$(".evaluationResultDiv tr>th:nth-child(5), .evaluationResultDiv tr>th:nth-child(6)").addClass(
							"alignRht nowrap");
					$(".evaluationResultDiv tr>td:nth-child(5), .evaluationResultDiv tr>td:nth-child(6)").addClass(
							"alignRht");
					$(".tableAwardAmount").autoNumeric('init', {
						vMax : '9999999999999999',
						vMin : '0.00'
					});
					if($("#isForCityTask").val() == "true")
					{
					document.getElementById("reassignDropDown").disabled = true;
					}
					 /* 
					  * disable all fields if user logged is different from task
					  * assigned to
					  **/
					var screenReadOnly = $("#screenReadOnly").val();
					if (screenReadOnly == 'true') {
						$("#saveButton").attr("class", "graybtutton");
						$("#saveButton").attr("disabled", "true");
						$("#internalComments").attr("disabled", "true");
						$("#cancelAwardButton").attr("class", "graybtutton");
						$("#cancelAwardButton").attr("disabled", "true");
						$("#finishButton").attr("class", "graybtutton");
						$("#finishButton").attr("disabled", "true");
						$(".localTabs.editAmountLink").click(function(e){
						   return false;
						});
					}
					else
					{
						$("#finishButton").attr("class", "button");
						$("#finishButton").removeAttr("disabled");
						$("#cancelAwardButton").attr("class", "redbtutton cancelbtn");
						$("#cancelAwardButton").removeAttr("disabled");
						enableDisableFinishButton();
						$(".localTabs.editAmountLink").click(function(e){
							   var providerName = $(e.target).closest("tr").find("td").eq(1).html();
							   var proposalTitle = $(e.target).closest("tr").find(".localTabs").html();
							   var evalScore = $(e.target).closest("tr").find("td").eq(3).html();
							   var amount = $(e.target).closest("tr").find("td").eq(4).text();
							   var selectComments = $(e.target).closest("tr").find('.selectionComments').val();
							   var negotiatedAmount = $(e.target).closest("tr").find('.editAmountLink').text();
							   var proposalId = $(e.target).closest("tr").find("td").eq(0).html();
							   $("#providerName").text(providerName); 
							   $("#propsalTitle").text(proposalTitle); 
							   $("#score").text(evalScore); 
							   $("#amount").text(amount);
							   if (negotiatedAmount != 'Edit Amount') {  
							   $('#finalAmount').val(negotiatedAmount);
							   }
							   $("#selectProposalId").val(proposalId);
							   $("#proposalComments").text(selectComments);
							   $(".overlay").launchOverlayNoClose($(".alert-box-editAward"), "600px", null);
							   $(".exit-panel").unbind('click').click(function(e){
									if(overlayLaunched != null){
										e.stopPropagation();
										overlayLaunched.closeOverlay(e);
										cancelOverLay();
										$("label.error").hide();
									}
								});
							});
						$("#cancelAwardButton").click(function(){
							pageGreyOut();
							var v_parameter = "&procurementId=" + $('#procurementId').val()
											+ "&contractID=" + $('#contractID').val() + "&organizationId=" + $('#providerId').val()
											+ "&evaluationPoolMappingId=" + $('#evaluationPoolMappingId').val();
							var urlAppender = $("#cancelAwardOverlay").val() + "&hideExitProcurement=false&" +
									"topLevelFromRequest=AwardsandContracts&" +
									"midLevelFromRequest=AwardsandContractsScreen&" +
									"implicitModel=true" ;
							jQuery.ajax({
								type : "POST",
								url : urlAppender,
								data : v_parameter,
								success : function(e) {
									removePageGreyOut();
									$("#requestCancel").html(e);
									$(".overlay").launchOverlay($(".alert-box-cancelAward"),
											$(".cancel-Award"), "600px", null);
								},
								error : function(data, textStatus, errorThrown) {
									showErrorMessagePopup();
									removePageGreyOut();
								}
							});
						});
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
																		.compareLocal(
																				$(
																						"form[name='"
																								+ lastDataArray[i][0]
																								+ "']")
																						.serializeArray())) {
																	isSame = false;
																}
															});
										}/*displays the dialogue if cancel is clicked without saving*/
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
																		deleteAutoSaveData();
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
 *  This function is used to save internal comments
 **/
function saveFinalizeAwardComments() {
	if (validateTextArea("internalComments")) {
		pageGreyOut();
		/*Start R5: UX module, clean AutoSave Data*/
		deleteAutoSaveData();
		/*End R5: UX module, clean AutoSave Data*/
		document.finalizeAwardForm.action = $("#saveFinalizeAwardComments").val();
		var options = {
	   		/*function finds the elements belonging to class overlaycontent*/
				success : function(responseText, statusText, xhr) {
					removePageGreyOut();
					 /*Defect 7397 changes*/
					var previousComment = $('#internalComments').val();
					$('#previousComments').val(previousComment);
					/* Defect 7397 changes ends*/
					var ignoreForms = [];
					updateSavedData(ignoreForms);
				},/*this function calls overlaycontent function*/
				error : function(xhr, ajaxOptions, thrownError) {
					showErrorMessagePopup();
					removePageGreyOut();
				}
			};
	  $(document.finalizeAwardForm).ajaxSubmit(options); 
	} else {
		removePageGreyOut();
		$("#ErrorDiv").html(invalidResponseMsg);
		$("#ErrorDiv").show();
	}
}

/**
 * This function is used to view proposal summary for selected proposal Id
 **/
function viewEditAmmount(proposalId) {
	var url = $("#viewProposalSummaryUrl").val() + "&proposalId=" + proposalId
			+ "&jspPath=evaluation/&removeMenu=asdas&fromAwardTask=true";
	window.open(url, 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

/**
 *  This function is used to view evaluation summary for selected proposal Id
 **/
function viewEvaluationSummary(proposalId) {
	window.open($("#viewEvaluationSummaryUrl").val() + "&proposalId="
			+ escape(proposalId) + "&procurementId="
			+ $("#procurementId").val() + "&evaluationPoolMappingId="
			+ $("#evaluationPoolMappingId").val() +"&removeMenu=asdas", 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
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
 * This function is used to reassign task to selected user
 **/
function reassignTask() {
	pageGreyOut();
	var reAssignUser = $('#reassignDropDown option:selected').html();
	$("#reassignedToUserName").val(reAssignUser);
	$("#finalizeAwardForm").attr(
			"action",
			$("#finalizeAwardReassignURL").val()
					+ "&submit_action=reassignFinalizeAwardTask");
	pageGreyOut();
	document.finalizeAwardForm.submit();
}

/** 
 * This function is used to reassign task to selected user
 **/
function finishTask() {
	var approveFinalizeFlag = false;
	$(".evaluationResultDiv table tr")
			.each(
					function() {
						if ($(this).find("td").size() == 6) {
							var orgAmount = new Big($(this).find("td").eq(4)
									.text().replaceAll(",", "").trim());
							var negotiatedAmount = new Big($(this).find("td")
									.eq(5).text().replaceAll(",", "").trim());
							if (negotiatedAmount.gte(orgAmount) || orgAmount.minus(orgAmount * 0.10).gte(negotiatedAmount)) {
								approveFinalizeFlag = true;
							}
						}
					});
	$('#ApprovedAmountFlag').val(approveFinalizeFlag);
	 document.finalizeAwardForm.action = $("#finishFinalizeAmountUrl").val();
	 document.finalizeAwardForm.submit();
	 $('#finishButton').prop('disabled', true);
}
/**
 * This function is executed to validate new amount 
 * entered by user and on success it will update the amount
 **/
function confirmAmount()
{	
	var proposalId = $('#selectProposalId').val();
	var finalAmount = $('#finalAmount').val().replaceAll(",", "");
	$('#finalAmount').val(finalAmount);
	pageGreyOut();
	document.finalizeAwardForm.action = $("#hiddenEditAmountOverlayUrl").val();
	var options = {
   		//function finds the elements belonging to class overlaycontent
			success : function(e) {
				removePageGreyOut();
				$(".overlay").closeOverlay($(".alert-box-editAward"));
				$('#finalAmount').val('');
				$( "tr:contains('"+proposalId+"')" ).find("a.localTabs label").text(e);
				$( "tr:contains('"+proposalId+"')" ).find("a.localTabs label").autoNumeric('update', {
					vMax : '9999999999999999',
					vMin : '0.00'
				});
				updateFinalTotalAmount();
				enableDisableFinishButton();
				var ignoreForms = [];
				updateSavedData(ignoreForms);
			},//this function calls overlaycontent function
			error : function(xhr, ajaxOptions, thrownError) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		};
  $(document.finalizeAwardForm).ajaxSubmit(options); 
}
/**
 * This function is used to close overlay
 **/
function cancelOverLay() {
	$(".overlay").closeOverlay();
	$('#finalAmount').val('');
	$("label.error").hide();
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
  * This function is used to return to task inbox or task management screen from
  *  which the user comes in
  **/
function returnToTaskInbox(taskId) {
	var returnVal="&returnToAgencyTask=true";
	$("#finalizeAwardForm").attr(
			"action",
			$("#finalizeAwardRenderUrl").val() + returnVal
					+ '&taskUnlock=' + taskId);
	document.finalizeAwardForm.submit();
}

$(window).load(function() {
	var ignoreForms = [];
	updateSavedData(ignoreForms);
});
//Method to compare.d
jQuery.fn.compareLocal = function(t) {
	if (this.length != t.length) { return false; }
	var a = this.sort(),
	b = t.sort();
	for (var i = 0; t[i]; i++) {
		if ($("*[name="+a[i].name+"]").closest(".skipElementsInCompare").size() == 0 && (a[i].name != b[i].name || a[i].value != b[i].value)) {
			return false;
		}
	}
	return true;
};
/**
 * This function is executed to update final total amount
 **/
function updateFinalTotalAmount(){
	var sum = new Big(0);
	$(".evaluationResultDiv table tr").each(function(){
		if($(this).find("td").size() == 6 && $(this).find("td").eq(5).find("a").text() != 'Edit Amount'){
			sum = sum.plus(new Big($(this).find("td").eq(5).find("a").text().replaceAll(",", "").trim()).toFixed(2));
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
 * This function is executed to update total estimated amount
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
/**
 * This function is executed to enable and disable finish button
 **/
function enableDisableFinishButton(){
	var enableButton = true;
	$(".evaluationResultDiv table tr").each(function(){
		if($(this).find("td").size() == 6 && $(this).find("td").eq(5).find("a").text() == 'Edit Amount')
		{
			enableButton = false;
		}
	});
	if(enableButton){
		$("#finishButton").attr("class", "button");
		$("#finishButton").removeAttr("disabled");
	}
	else{
		$("#finishButton").attr("class", "graybtutton");
		$("#finishButton").attr("disabled", "true");
	}
}