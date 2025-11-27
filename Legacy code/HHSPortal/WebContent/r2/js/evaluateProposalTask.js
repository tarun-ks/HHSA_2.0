/**
 * This file contains methods that will handle the task
 * #Evaluate Proposal Task.
 * This contains method that will handle the form display,
 * view document information.
 */
var lastDataArray = new Array();
var invalidRespMsg = "Your response contains invalid characters. Please re-type your response or visit Online Help for instructions on how to Copy and Paste as plain text.";
var invalidCharsUsed = invalidRespMsg + " Navigate to the Scores & Comments tab to correct the errors before finishing this task.";
/**
*  This function is used to render the status of the screen on load
*  Updated Method in R4
*/
$(document)
		.ready(
				function() {
					$('[id^="scoreSeqNum"]').change(function(){
						var scoreId = $(this).attr("id");
						var scoreSeq = scoreId.charAt(12);
						if($("#scoreHidden" + scoreSeq + "").val() == $(this).val()){
							$("#scoreAmendedId" + scoreSeq + "").val("no");
						} else {
							$("#scoreAmendedId" + scoreSeq + "").val("yes");
						}
					});
					
					$('#contractTabs li')
							.removeClass(
									'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
					$("#contractTabs").tabs();
					$("#authenticate").hide();
					$("#finishButton").attr("class", "graybtutton");
					$("#finishButton").attr("disabled", "true");
					$("#contractTabs").find('a').each(function() {
						$(this).click(function() {
							if ($(this).hasClass('showButton')) {
								$("#saveDiv").show();
							} else {
								$("#saveDiv").hide();
							}
						});
					});

					/* disable all fields if logging from city user
					 Start of Changes done for enhancement QC : 5688 for Release 3.2.0*/
					var screenReadOnly = $("#screenReadOnly").val();
					if (screenReadOnly == 'true') {
						$("#validateCheckbox").attr("disabled", "true");
						$("#saveButton").attr("class", "graybtutton");
						$("#saveButton").attr("disabled", "true");
						$("#internalComments").attr("disabled", "true");
						$(".txt").attr("disabled", "true");
						$("textarea").attr("readonly", "readonly");
					}
					/* End of Changes done for enhancement QC : 5688 for Release 3.2.0*/
					
					$("#validateCheckbox").change(function() {
						if ($(this).is(':checked')) {
							$("#authenticate").show();
							$("#finishButton").attr("class", "button");
							$("#finishButton").removeAttr("disabled");
						} else {
							$("#authenticate").hide();
							$("#finishButton").attr("class", "graybtutton");
							$("#finishButton").attr("disabled", "true");
						}
					});

					$("#validateCheckbox").removeAttr("checked");

					/* iterate through each textbox and add keyup
					 handler to trigger sum event*/
					$(".txt").each(function() {

						$(this).each(function() {
							calculateTotalScore();
						});
					});

					$(".txt").each(function() {

						$(this).keyup(function() {
							calculateTotalScore();
						});
					});/*for font size change*/
					$("a[id!='smallA'][id!='mediumA'][id!='largeA'][id!='helpIcn']").click(function(e) {
						var $self=$(this);
						var isSame = true;
						if(lastDataArray != null && lastDataArray.length > 0){
							$.each(lastDataArray, function(i) {
								if(!$(lastDataArray[i][1]).compareLocal($("form[name='"+lastDataArray[i][0]+"']").serializeArray())){
									isSame = false;
								}
							});
						}
						/* Displays a popup if cancel is clicked with unsaved data*/
						if(!isSame && lastDataArray != null && lastDataArray.length > 0 && !$(this).hasClass("localTabs") && !$(this).hasClass("taskSelected") && !$(this).hasClass("taskNormal")){
							e.preventDefault();
							$('<div id="dialogBox"></div>').appendTo('body')
							.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
							.dialog({
								modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
								width: 'auto', modal: true, resizable: false, draggable:false,
								dialogClass: 'dialogButtons',
								buttons: {
									OK: function () {
										/*Start R5: UX module, clean AutoSave Data*/
										deleteAutoSaveData();
										/*End R5: UX module, clean AutoSave Data*/
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
					
					if($("#fromSaveButton").val()!=null && $("#fromSaveButton").val()=='true'){
						$("#contractTabs").tabs('select', 2);
						$("#saveDiv").show();
					}else if($("#confirmScoresTab").val()!=null && $("#confirmScoresTab").val()=='true'){
						$("#contractTabs").tabs('select', 3);
						$("#saveDiv").hide();
					}
					else{
						$("#saveDiv").hide();
					}
				});

/** This function is used to view procurement summary for agency/provider users*/
function viewProcurementSummary(procurementId) {
	var url = $("#procurementSummaryURL").val()+"&action=procurementHandler&overlay=true";
	window.open(url,
			'windowOpenTab',
	'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}


/** This method is used to preview the document in the frame*/
function previewUrl(url, target) {
	clearTimeout(window.ht);
	window.ht = setTimeout(
			function() {
				var div = document.getElementById(target);
				div.innerHTML = '<iframe style="width:100%; height:550px; display:block;"  src="'
						+ url + '" />';
			}, 20);
}

/** This method is used to view the Document Info*/
function viewDocumentInfo(documentId,documentType, currentObj) {
	$("#linkDiv").attr("style","display:none");
	var orgType;
	var divId = $(currentObj).closest("div").parent().attr("id");
	if (divId == 'ProposalDocuments') {
		orgType = 'provider_org';
	}
	if (divId == 'RFPDocuments') {
		orgType = 'city_org';
	}
	$("#hiddenDocumentId").val(documentId);
	$("#docTypeHidden").val(documentType);
	$("#orgType").val(orgType);
	/*Start || Modified as a part of Enhancement #5688 For Release 3.2.0*/
	if($("#isForCityTask").val() == "true")
	{ 
		$("#controller_action").val("agencyWorkflowCity");
	}
	/*End || Modified as a part of Enhancement #5688 For Release 3.2.0*/
	$("#evaluateProposalForm").attr("action", $("#evaluateProposalUrl").val());
	$("#submit_action").val("viewDocumentInfo");
	var options = {
		success : function(responseText, statusText, xhr) {
			var $response = $(responseText);
			var data = $response.contents().find(".overlaycontent");
			$("#viewDocumentProperties").html(data.detach());
			$(".overlay").launchOverlay($(".alert-box-viewDocumentProperties"),
					$(".exit-panel.upload-exit"), "600px", null, "onReady");
			$('ul')
					.removeClass(
							'ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
			$('li')
					.removeClass(
							'ui-state-default ui-corner-top ui-state-hover');
			removePageGreyOut();
		},
		error : function(xhr, ajaxOptions, thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	pageGreyOut();
	$("#evaluateProposalForm").ajaxSubmit(options);
}

/** This function is used to view proposal summary for selected proposal*/
function viewProposalSummary(proposalId, procurementId) {
	/*Start || Modified as a part of Enhancement #5688 For Release 3.2.0*/
	var returnVal;
	if($("#isForCityTask").val() == "true")
	{
		returnVal="&controller_action=agencyWorkflowCity";
	}
	else
	{
		returnVal="";
	}
	/*End || Modified as a part of Enhancement #5688 For Release 3.2.0*/
	removeTaskSelectedClass();
	document.getElementById(proposalId).className = "taskSelected";
	previewUrl($("#viewProposalSummaryUrl").val()
			+ "&jspPath=procurement/evaluation/&removeMenu=asdas" + returnVal, 'linkDiv');
}

/** This method is used to calculate the total score as the user enters the score
 in the score textbox*/
function calculateTotalScore() {
	var totalScore = 0;
	/* iterate through each textbox and add the values*/
	$(".txt").each(function() {
		/* add only if the value is number*/
		if (!isNaN(this.value) && this.value.length != 0) {
			totalScore += parseInt(this.value);
		}

	});
	$("#totalScore").html(totalScore);
}

/** This method is used to calculate the total score for the Maximum Score column*/
function calculateTotalMaxScore() {

	var totalMaxScore = 0;
	/* iterate through each td based on class and add the values*/
	$(".maxScore").each(function() {
		var value = $(this).text();
		/* add only if the value is number*/
		if (!isNaN(value) && value.length != 0) {
			totalMaxScore += parseInt(value);
		}

	});
	$("#totalMaxScore").html(totalMaxScore);
};

/**This function is used to finish the Evaluation Task
 * Method modified as per enhancement 5415
 * Updated Method in R4
 * Method modified as per enhancement 5415
 */
function finishEvaluationTask() {
	$(".error").html("");
	$("#failedmessagediv").html("");
	$("#failedmessagediv").hide();
	$("#errordiv").html("");
	$("#errordiv").hide();
	var errorCount = 0;
	var internalCommentValid = false;
	var internalComments = $("#internalComments").val();
	var idCount = 0;
	var errorScoreCommentCount = 0;

	/*check 1 :validates that userid and pwd are not blank  */
	if (!validateUsernameAndPassword()) {
		return false;
	}
	/*check 1 : end
	check 2: validates that a)scores should be entered, b) scores should not be greater than max score, c) score comments are not blank
*/	$(".scoreAndCommentRow")
		.each(
			function() {
				var proposalScore = $(this).find(".txt").val();
				var maximumScore = $.trim($(this).find("#maxScore").text().replace('/',''));
				var scoreComments = $(this).next().find("textarea").val();
				var lberror = false;
				
				if (typeof proposalScore != "undefined" && (($.trim(proposalScore).length == 0)
						|| (parseInt(proposalScore) > parseInt(maximumScore))
						|| (scoreComments == null || scoreComments == '' || $.trim(scoreComments).length ==0 )
						)) {
					$(this).find("#missingScoreOrCmnts").html("! ").attr("style", "display:inline; margin-right:3px");
					errorCount++;
					$('html,body').scrollTop(0);
					$("#failedmessagediv").text("All scores and comments are required and cannot exceed the maximum score. Navigate to the Scores & Comments tab to correct the errors before finishing this task.");
					$("#failedmessagediv").show();
				}
			});
	/*check 2 : end
	check 3: validates that general comment are not blank*/
	if (internalComments == null || internalComments == '' || $.trim(internalComments).length ==0) {
		$('html,body').scrollTop(0);
		$("#failedmessagediv").text("All scores and comments are required and cannot exceed the maximum score. Navigate to the Scores & Comments tab to correct the errors before finishing this task.");
		$("#failedmessagediv").show();
		$("#internalCmnts").html("! ").attr("style", "display:inline; margin-right:3px");
		errorCount++;
	}
	/*check 3: end
	check 4: Check if no error is present then proceed with further validation in else part
*/	if (errorCount > 0) {
		return false;
	}else{
		/*check 4.1 :validates that special chars that are not allowed should not be entered at score level comments*/
		$(".scoreAndCommentRow")
			.each(
				function() {
					if(!validateTextArea("comments_"+idCount)){
						errorScoreCommentCount ++;
						$(this).find("#missingScoreOrCmnts").html("! ").attr("style", "display:inline; margin-right:3px");
					}
					idCount ++;
				});
		/*check 4.1: end
		check 4.2 :validates that special chars that are not allowed should not be entered at General level comments*/
		if(!validateTextArea("internalComments")){
			internalCommentValid = false;
			$("#internalCmnts").html("! ").attr("style", "display:inline; margin-right:3px");
		}else{
			internalCommentValid = true;
		}
		/*check 4.2: end*/
	}
	/*check 4 : end
	check 5 : score level comments and general comments does not contain chars that are not allowed*/
	if((internalCommentValid) && (parseInt(errorScoreCommentCount)) == 0 ){
		$("#evaluateProposalForm").attr(
				"action",
				$("#evaluateProposalForm").attr("action")
						+ "&submit_action=finishEvaluateProposalTask");
		pageGreyOut();
		document.evaluateProposalForm.submit();
	} else {
		removePageGreyOut();
		$('html,body').scrollTop(0);
		$("#failedmessagediv").html(invalidCharsUsed);
		$("#failedmessagediv").show();
	}
	/*check 5 : end*/
}

/** This function is called when the user clicks on the Save button and is used
  * to save the evaluation details
  * Method modified as per enhancement 5415
  * Updated Method in R4
  * Method modified as per enhancement 5415
  */
function saveEvaluationTask() {
	$(".error").html("");
	$("#failedmessagediv").html("");
	$("#failedmessagediv").hide();
	$("#errordiv").html("");
	$("#errordiv").hide();
	var errorCount = 0;
	var idCount = 0;
	var errorScoreCommentCount = 0;
	var internalCommentValid = false;
	var internalComments = $("#internalComments").val();

	/*check 1 :validates that special chars that are not allowed should not be entered at score level comments*/
	$(".scoreAndCommentRow")
	.each(
		function() {
			var scoreComments = $(this).next().find("textarea").val();
			if(!validateTextArea("comments_"+idCount)){
				errorScoreCommentCount ++;
				$(this).find("#missingScoreOrCmnts").html("! ").attr("style", "display:inline; margin-right:3px");
			}
			idCount ++;
		});
	/*check 1: end
	check 2 :validates that special chars that are not allowed should not be entered in General comments*/
	if(!validateTextArea("internalComments")){
		internalCommentValid = false;
		$("#internalCmnts").html("! ").attr("style", "display:inline; margin-right:3px");
	}else{
		internalCommentValid = true;
	}
	/*check 2 : end
	check 3 :check is all comments are valid then save details else show error message*/
	if((internalCommentValid) && (parseInt(errorScoreCommentCount)) == 0 ){
	/*	Start R5: UX module, clean AutoSave Data*/
		deleteAutoSaveData();
		/*End R5: UX module, clean AutoSave Data*/
		$("#evaluateProposalForm").attr(
				"action",
				$("#evaluateProposalForm").attr("action")
						+ "&submit_action=saveEvaluateProposalTaskDetails");
		var ignoreForms = [];
		updateSavedData(ignoreForms);
		pageGreyOut();
		document.evaluateProposalForm.submit();
	} else {
		removePageGreyOut();
		$('html,body').scrollTop(0);
		$("#failedmessagediv").html(invalidRespMsg);
		$("#failedmessagediv").show();
	}
	/*check 3: end*/
}
/** This function is used to validate username and password*/
function validateUsernameAndPassword(){
	var isValidated = true;
	var username = $("#userName").val();
	var password = $("#password").val();
	if(null == username || "" == username){
		$("#userName").parent().next().html("! This field is required");
		isValidated = false;
	}
	if(null == password || "" == password){
		$("#password").parent().next().html("! This field is required");
		isValidated = false;
	}
	return isValidated;
}
/** This function will remove task selected class from other proposal links*/
function removeTaskSelectedClass() {
	var arrowDiv = document.getElementsByName("taskArrow");
	for (var i = 0; i <	 arrowDiv.length; i++)
	{
		arrowDiv[i].className = "taskNormal";
	}
}

 /**
  * This function is used to return to task inbox or task management screen from
  * which the user comes in
  */
function returnToAgencyTaskList(taskId) {
	/**Start || Modified as a part of Enhancement #5688 For Release 3.2.0*/
	var returnVal;
	if($("#isForCityTask").val() == "true")
	{
		returnVal="&returnToAgencyTask=true&controller_action=agencyWorkflowCity";
	}
	else
	{
		returnVal="&returnToAgencyTask=true";
	}
	$("#evaluateProposalForm").attr(
			"action",
			$("#evaluateProposalRenderUrl").val() + returnVal
					+ '&taskUnlock=' + taskId);
	/**End || Modified as a part of Enhancement #5688 For Release 3.2.0*/
	document.evaluateProposalForm.submit();
}

/** This function will close view document info overlay */
function closeOverLayInfo(){
	$("#linkDiv").attr("style","display:block");
}

$(window).load(function(){
	var ignoreForms = [];
	updateSavedData(ignoreForms);
});
/**Method to compare.d*/
jQuery.fn.compareLocal = function(t) {
	if (this.length != t.length) { return false; }
	var a = this.sort(),
	b = t.sort();
	for (var i = 0; t[i]; i++) {
		if ($("*[name="+convertValue(a[i].name)+"]").closest(".skipElementsInCompare").size() == 0 && (a[i].name != b[i].name || a[i].value != b[i].value)) {  
			return false;
		}
	}
	return true;
};

/**
 * This function escapes special characters
 * Updated Method in R4
 */
function convertValue(id){
	if(id.indexOf('[') != -1 || id.indexOf(']') != -1 || id.indexOf('.') != -1 )
	{
		var test = id.replace(/[[]/g,'\\[');
	    test = test.replace(/]/g,'\\]');
	    test = test.replace(/./g,'\\.');
	    return test;
	}
    return id;
}