var lastDataArray = new Array();
/**
 *  This function is used to render the status of the screen on load
 *   Updated Method in R4
 * */
$(document)
		.ready(
				function() {
					var procurementId = $("#procurementId").val();
					var proposalId = $("#proposalId").val();
					var organizationId = $("#organizationId").val();
					changeReviewStatus();
					$(".actionDropDown").change(function(){
						disableFinishButtonOnChange(this);	
					});
					$('#contractTabs li')
							.removeClass(
									'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
					$("#contractTabs").tabs();
					$("#contractTabs").find('a').each(function() {
						$(this).click(function() {
							if ($(this).hasClass('showButton')) {
								$("#saveDiv").show();
							} else {
								$("#saveDiv").hide();
							}
						});
					});

					// iterate through each textbox and add keyup
					// handler to trigger sum event
					$(".score").each(function() {
						$(this).each(function() {
							calculateTotalScore();
						});
					});
					$(".totalScoreInComment").each(function(i) {
						$(".totalScoreComment").eq(i).html($(this).html());
					});
					$(".maxScore").each(function() {
						$(this).each(function() {
							calculateTotalMaxScore();
						});
					});
					// saves the review score tasks details
					$("#saveButton").click(function(){
						//Start R5: UX module, clean AutoSave Data
						deleteAutoSaveData();
						//End R5: UX module, clean AutoSave Data
						if(validateTextArea("internalComments")){
							$("#reviewEvaluationForm").attr(
									"action",
									$("#actionUrl").val()
											+ "&submit_action=saveReviewScoresTaskDetails");
							pageGreyOut();
						     var options = {
		                				success : function(responseText, statusText, xhr) {
		                					removePageGreyOut();
		                					var ignoreForms = [];
		                					updateSavedData(ignoreForms);
		                					changeReviewStatus();
		                				},
		                				error : function(xhr, ajaxOptions, thrownError) {
		                					showErrorMessagePopup();
											removePageGreyOut();
		                				}
		                			};
							$("#reviewEvaluationForm").ajaxSubmit(options);
						} else {
							removePageGreyOut();
							$("#ErrorDiv").html(invalidResponseMsg);
							$("#ErrorDiv").show();
						}
					});
					// finishes review scores tasks
					$("#finishTask").click(function(){
						if(validateTextArea("internalComments")){
							$("#reviewEvaluationForm").attr(
									"action",
									$("#actionUrl").val()
									// Start Updated in R5
											+ "&submit_action=finishReviewScoresTask&score="+$("#avgtotalScore").val());
									// End Updated in R5
							pageGreyOut();
							document.reviewEvaluationForm.submit();
						} else {
							removePageGreyOut();
							$("#ErrorDiv").html(invalidResponseMsg);
							$("#ErrorDiv").show();
						}
					});//opens up organisation summary
					$("#organizationLink").click(function(){
						var urlAppend = "&organizationId="+organizationId;
						window.open($("#hiddenOrganizationSummary").val()+urlAppend,
								'windowOpenTab',
								'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
					});
					checkAndDisable();
					// add styles to the table
					$("#averageScoresTable").find("tr").each(function(i){
						if(i!=0){
							if(i%2 == 0){
								$(this).addClass("oddRows");
							}else{
								$(this).addClass("evenRows");
							}
						}
					});
					$("#evaluatorCommentsTable").find("tr").each(function(i){
						if(i!=0){
							if(i%2 == 0){
								$(this).addClass("oddRows");
							}else{
								$(this).addClass("evenRows");
							}
						}
					});
					$("#reassignButton").attr("disabled", "true");
					$("#reassignButton").attr("class", "graybtutton");
					
					$("a[id!='smallA'][id!='mediumA'][id!='largeA'][id!='nameLink']").click(function(e) {
						var $self=$(this);
						var isSame = true;
						if(lastDataArray != null && lastDataArray.length > 0){
							$.each(lastDataArray, function(i) {
								if(!$(lastDataArray[i][1]).compare($("form[name='"+lastDataArray[i][0]+"']").serializeArray())){
									isSame = false;
								}
							});
						}// displays the dialogue box data is unsaved
						if(!isSame && lastDataArray != null && lastDataArray.length > 0 && !$(this).hasClass("localTabs")){
							e.preventDefault();
							$('<div id="dialogBox"></div>').appendTo('body')
							.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
							.dialog({
								modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
								width: 'auto', modal: true, resizable: false, draggable:false,
								dialogClass: 'dialogButtons',
								buttons: {
									OK: function () {
										//Start R5: UX module, clean AutoSave Data
										deleteAutoSaveData();
										//End R5: UX module, clean AutoSave Data
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
				});

/**
 * This method is used to calculate the total score as the user enters the score
 * in the score textbox
 *  Updated Method in R4
 **/
function calculateTotalScore() {
	var totalScore = 0;
	// iterate through each textbox and add the values
	// class name change as per enhancement 4515 of release 2.5.0
	$(".realscore").each(function() {
		var value = $(this).text();
		// add only if the value is number
		if (!isNaN(value) && value.length != 0) {
			totalScore += parseFloat(value);
		}

	});
	$("#totalScore").html(totalScore.toFixed(2));
}

/** 
 * This method is used to calculate the total score for the Maximum Score column
 * */
function calculateTotalMaxScore() {
	var totalMaxScore = 0;
	// iterate through each td based on class and add the values
	$(".maxScore").each(function() {
		var value = $(this).text();
		// add only if the value is number
		if (!isNaN(value) && value.length != 0) {
			totalMaxScore += parseInt(value);
		}

	});
	$("#totalMaxScore").html(totalMaxScore);
};

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
 *  This function is used to reassign task to selected user
 **/
function reassignTask() {
	pageGreyOut();
	var reAssignUser = $('#reassignDropDown option:selected').html();
	$("#reassignedToUserName").val(reAssignUser);
	$("#reviewEvaluationForm").attr(
			"action",
			$("#actionUrl").val()
					+ "&submit_action=reassignReviewScoresTask");
	pageGreyOut();
	$("#reviewEvaluationForm").submit();
}

/**
 * this method changes the status of task
 **/
function changeReviewStatus(){
	var value = "In Review";
	$("#finishTask").attr("disabled", true);
	if($(".actionDropDown option:selected[value=' ']").size()>0){
		value = $("#onloadStatus").val();
	}else if($(".actionDropDown option:selected[value='2']").size()>0){
		value = "Scores Returned";
		$("#finishTask").attr("disabled", false);
		$("#finishTask").attr("class", "button");
	}else{
		value = "Scores Accepted";
		$("#finishTask").attr("disabled", false);
		$("#finishTask").attr("class", "button");
	}
	$("#taskStatus").val(value);
	$("#reviewStatusFinish").html(value);
}

/**
 * Check if screen is editable and disable accordingly
 **/
function checkAndDisable(){
	var disableScreen = $("#screenReadOnly").val();
	if(disableScreen == "true"){
		$(".actionDropDown").attr("disabled", disableScreen);
		$("textarea").attr("disabled", disableScreen);
		$("#saveButton").attr("disabled", disableScreen);
		$("#finishTask").attr("disabled", disableScreen);
	}
}

/**
 * This function is used to return to task inbox or task management screen from
 *  which the user comes in
 */
function returnToAgencyTaskList(taskId) {
	var returnVal;
	if($("#isForCityTask").val() == "true")
	{
		returnVal="&returnToAgencyTask=true&controller_action=agencyWorkflowCity";
	}
	else
	{
		returnVal="&returnToAgencyTask=true";
	}
	$("#reviewEvaluationForm").attr(
			"action",
			$("#reviewScoresRenderUrl").val() + returnVal
					+ '&taskUnlock=' + taskId);
	document.reviewEvaluationForm.submit();
}

/** 
 * This function is used to disable button on changing assign status value
 *  this function is modified as part of defect 5631 for release 2.6.0
 **/
function disableFinishButtonOnChange(currentObj) {
	// commented above part and  added below lines as part of defect 5631 for release 2.6.0
	$("#finishTask").attr("class", "graybtutton");
	$("#finishTask").attr("disabled", "true");
	
}

/**
 * This function is used to view procurement summary for agency/provider users
 **/
function viewProcurementSummary() {
	var url = $("#procurementSummaryURL").val()+"&action=procurementHandler&overlay=true";
	window.open(url,
			'windowOpenTab',
	'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}
/**
 * This function is used to view proposal summary for selected proposal Id
 **/
function viewProposalDetail(proposalId) {
	//Start || Modified as a part of Enhancement #5688 For Release 3.2.0
	var returnVal;
	if($("#isForCityTask").val() == "true")
	{
		returnVal="&controller_action=agencyWorkflowCity";
		jspPathVal ="&jspPath=evaluation/";
	}
	else
	{
		returnVal="";
		jspPathVal ="&jspPath=procurement/evaluation/";
	}
	var url = $("#viewProposalSummaryUrl").val() + "&proposalId=" + proposalId
			+ "&removeMenu=asdas&IsProcDocsVisible=true" + returnVal + jspPathVal;
	//End || Modified as a part of Enhancement #5688 For Release 3.2.0
	window.open(url,
			'windowOpenTab',
	'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

$(window).load(function(){
	var ignoreForms = [];
	updateSavedData(ignoreForms);
});

/**
 *  This function will open the criteria and comments popup on click of evaluator name.
 *   Updated Method in R4
 */
function showCommentsPopup(evaluationStatusId,evaluatorName){
	pageGreyOut();
	// R5 code starts to add procurementId
	var v_parameter = "evaluationStatusId=" + evaluationStatusId+"&evaluatorName=" + evaluatorName+"&procurementId="+$('#procurementId').val()+"&proposalId="+$('#proposalId').val();
	// R5 code ends to add procurementId
	// Start || Modified as a part of Enhancement #5688 for Release 3.2.0
	if($("#isForCityTask").val() == "true")
	{ 
		$("#controller_action").val("agencyWorkflowCity");
	}
	// End || Modified as a part of Enhancement #5688 for Release 3.2.0
	var urlAppender = $("#evaluatorCommentsUrlId").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(responseText) {
			$("#overlayDivId").html(responseText);
			$(".overlay").launchOverlayNoClose($(".alert-box-amend-contract"),
					"850px", null, "onReady");
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
			});
			removePageGreyOut();
			},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			 removePageGreyOut();
		}
	});
}
/** 
 * This method clears and closes overLay
 **/
function clearAndCloseOverLay() {
	$("#overlayDivId").html("");
	$(".overlay").closeOverlay();
}

/**
 * Added in R5 to show/hide Average Proposal Scores part
 **/
function showEvalInfo(obj, round){
	$('.'+obj).toggle();
	if($('#'+round).find('.container3').css('display') != "none"){
		$('#'+round).find('div.container3').hide();
		$('#'+round).find('div.container4').show();
	}else{
		$('#'+round).find('div.container3').show();
		$('#'+round).find('div.container4').hide();
	}
}