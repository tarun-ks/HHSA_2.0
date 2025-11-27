/** 
 * This js file is used for Agency setting module across 2 main components
 1. S404 screen -- city user login
 2. S405 screen -- agency user login
 *Javascript for filter popup
 *Updated In R6 for Return Payment.
 */
function setVisibility(id, visibility) {
	document.getElementById(id).style.display = visibility;
}
// Start Added in R5
var currentSelectedLevel;
/** End Added in R5
 **********************************************************************************************
 ** 1. S404 screen -- city user login **
 **********************************************************************************************
 Desc : This function is called when radio button state is changed on initial
 load*/
$(document).ready(function() {
	$('input[name="rdoSettings"]').change(function() {
		$("#saveLevels").removeAttr('disabled');
	});
	//Added R6 - Returned payment to show error message if any
	$(".messagediv")
	.html(
			responsesArr[3]
					+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
	$(".messagediv").addClass(
	responsesArr[4]);
	if(_messageType != ""){
		$(".messagediv").show();
	}
	//Ended R6
});
/**
 * This mouseup function is added in R6 for Bulk Notification 
 * for hiding a div on clicking outside of it
 */
$(document).mouseup(function (e)
		{
		    var container = $('#bulkNotificationDiv');

		    if (!container.is(e.target) // if the target of the click isn't the container...
		        && container.has(e.target).length === 0) // ... nor a descendant of the container
		    {
		        container.hide();
		    }
		});

		$(document).mouseup(function (e)
				{
				    var container = $('#bulkNotificationInformationDiv');

				    if (!container.is(e.target) // if the target of the click isn't the container...
				        && container.has(e.target).length === 0) // ... nor a descendant of the container
				    {
				        container.hide();
				    }
				});
		/**
		 * End
		 */
/** 
 * Desc : This function is called for enabling/disabling of views
 * */
function enableDisableId(elementId) {
	// below condition is for agency select box
	if (null != elementId.id && elementId.id == "agencySelectBox") {
		hideTransactionStatusDiv();
		$("#reviewProcessSelect")[0].selectedIndex = 0;
		if ($('#goButton').is(':enabled')) {
			$("#goButton").attr('disabled', 'disabled');
			disableLevelsOfReviewDiv();
		}

		if ($("#agencySelectBox").val() != "Select an Agency") {
			$("#reviewProcessSelect").removeAttr('disabled');
		} else {
			$("#reviewProcessSelect").attr('disabled', 'disabled');
		}
	}

	// below condition is for go button
	if (null != elementId.id && elementId.id == "reviewProcessSelect") {
		hideTransactionStatusDiv();
		$("#goButton").attr('disabled', 'disabled');
		disableLevelsOfReviewDiv();

		if ($("#reviewProcessSelect").val() != "rps0") {
			$("#goButton").removeAttr('disabled');
			$("#reviewProcDesc").html(document.getElementById("reviewProcessSelect").options[document.getElementById("reviewProcessSelect").options.selectedIndex].id);
		} else {
			$("#goButton")[0].selectedIndex = 0;
		}
	}
}

/**
 *  Desc : This function is for disabling levels of review div
 *  */
function disableLevelsOfReviewDiv() {
	$('input[name="rdoSettings"]').prop('checked', false);
	$("#levelsOfReviewDiv").hide();
	$("#saveLevels").attr('disabled', 'disabled');
}

/**
 *  Desc : This function is called when city user selects agency --> review
 process --> Go button
 It makes an ajax call to fetch review level if exist in database and auto
 selects radio button
 */
function ajaxCallForLevelsOfReview() {
	hideTransactionStatusDiv();
	pageGreyOut();
	$("#saveLevels").attr('disabled', 'disabled');
	var agencySelectId = document.getElementById("agencySelectBox");
	var reviewSelectId = document.getElementById("reviewProcessSelect");
	var agencyId = agencySelectId.options[agencySelectId.options.selectedIndex].value;
	var reviewProcessId = reviewSelectId.options[reviewSelectId.options.selectedIndex].value;
	var reviewTaskName = reviewSelectId.options[reviewSelectId.options.selectedIndex].text;
	var v_parameter = "hdnAgencyId=" + agencyId + "&hdnReviewProcId="
			+ reviewProcessId;
	var urlAppender = $("#hiddenLevelsOfReviewURL").val();
	$("#sp1").html(agencyId);
	$("#sp3").html(agencyId);
	$("#sp4").html(agencyId);
	$("#sp5").html(agencyId);
	$("#sp6").html(reviewTaskName);
	$("#sp7").html(reviewTaskName);
	$("#sp8").html(reviewTaskName);
	$("#sp2").html(reviewTaskName);
	$("#levelofRevw").html(reviewTaskName);
	
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			removePageGreyOut();
			var revLevel = e.split("::");
			autoSelectReviewLevel(revLevel[1]); // method called for auto
			// selection
			// Start Added in R5
			currentSelectedLevel=$('#NumberOfLevels li input:checked').val();
			// End Added in R5
		},
		beforeSend : function() {
		}
	});
}

/**
 *  Desc : This function is called inside ajaxCallForLevelsOfReview to auto
 select radio button with review level already present in database
 */
function autoSelectReviewLevel(revLevel) {
	switch (revLevel) {
	case '2':
		document.getElementById('rdoSettings1').checked = true;
		break;
	case '3':
		document.getElementById('rdoSettings2').checked = true;
		break;
	case '4':
		document.getElementById('rdoSettings3').checked = true;
		break;
	}
	$("#levelsOfReviewDiv").show();
}

/**
 *  Desc : This function is called when city user selects agency --> review
 process --> assigns review level and clicks on save button
 It makes an ajax call to save review level in database
changes done for Enhancement 6534 for Release 3.8.0
*/
function ajaxCallToSaveReviewLevels() {
	// Start Updated in R5
	if(parseInt(currentSelectedLevel) > parseInt($('#NumberOfLevels li input:checked').val())){
		$('<div id="dialogBox"></div>').appendTo('body')
        .html('<div>Are you sure you want to decrease the levels of review for this task? This action will remove default user assignments on the eliminated level for all contracts for this task.</div>')
        .dialog({
              modal: true, title: '', zIndex: 10000, autoOpen: true,
              width: 'auto', modal: true, resizable: false, draggable:false,
              dialogClass: 'dialogButtons',
              buttons: {
                    Yes: function () {//submits  form
                    	saveMethod();
                    	$(this).dialog("close");
                    },
                    No: function () {// closes dialogue box
                 	   $(this).dialog("close");
                    }
              },
              close: function (event, ui) {
                    $(this).remove();
              }
        });
		$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
	}else{
		saveMethod();
	}
}
// End Updated in R5

/**
 * This function is used to save details
 */
function saveMethod() {
	$("#saveLevels").attr('disabled','disabled');
	var agencySelectId = document.getElementById("agencySelectBox");
	var reviewSelectId = document.getElementById("reviewProcessSelect");

	var selectedRevLevel = $("input[type='radio'][name='rdoSettings']:checked");
	var agencyId = agencySelectId.options[agencySelectId.options.selectedIndex].value;
	var reviewProcessId = reviewSelectId.options[reviewSelectId.options.selectedIndex].value;
	// START || changes done for Enhancement 6534 for Release 3.8.0
	var taskType = reviewSelectId.options[reviewSelectId.options.selectedIndex].title;
	var v_parameter = "hdnAgencyId=" + agencyId + "&hdnReviewProcId="
			+ reviewProcessId + "&hdnReviewLevels=" + selectedRevLevel.val()+ "&taskType=" + taskType;
	// END || changes done for Enhancement 6534 for Release 3.8.0
	var urlAppender = $("#hiddenSaveLevelsOfRevURL").val();
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			if (e != null) {
				var msg = e.split("#");
				if (msg[0] == "success") {
					$("#transactionStatusDiv").html(msg[1]);
					$("#transactionStatusDiv").addClass('passed');
					$("#transactionStatusDiv").show();
				} else if (msg[0] == "failure") {
					$("#transactionStatusDiv").html(msg[1]);
					$("#transactionStatusDiv").addClass('failed');
					$("#transactionStatusDiv").show();
				}
			}
			removePageGreyOut();
		},
		beforeSend : function() {
		}
	});
}

/** 
 * Below function hide error status div(this happens when user changes or takes
 another action)
 */
function hideTransactionStatusDiv() {
	$("#transactionStatusDiv").hide();
	$("#transactionStatusDiv").html("");
	$("#transactionStatusDiv").removeClass('failed');
	$("#transactionStatusDiv").removeClass('passed');
}

/**
 *************************************************************************************************
 ** 2. S405 screen -- agency user login **
 *************************************************************************************************
 Desc : This function enables remove button when items are selected to their
 corresponding select box*/
function enableremovebtn(elementId) {
	if (null != elementId.id
			&& (elementId.id == "level1Users" || elementId.id == "level1Remove")) {
		if ($("select[id='level1Users'] option:selected").index() > -1) {
			$("#level1Remove").removeAttr('disabled');
		} else {
			$("#level1Remove").attr('disabled', 'disabled');
		}
	} else if (null != elementId.id
			&& (elementId.id == "level2Users" || elementId.id == "level2Remove")) {
		if ($("select[id='level2Users'] option:selected").index() > -1) {
			$("#level2Remove").removeAttr('disabled');
		} else {
			$("#level2Remove").attr('disabled', 'disabled');
		}
	} else if (null != elementId.id
			&& (elementId.id == "level3Users" || elementId.id == "level3Remove")) {
		if ($("select[id='level3Users'] option:selected").index() > -1) {
			$("#level3Remove").removeAttr('disabled');
		} else {
			$("#level3Remove").attr('disabled', 'disabled');
		}
	} else if (null != elementId.id
			&& (elementId.id == "level4Users" || elementId.id == "level4Remove")) {
		if ($("select[id='level4Users'] option:selected").index() > -1) {
			$("#level4Remove").removeAttr('disabled');
		} else {
			$("#level4Remove").attr('disabled', 'disabled');
		}
	}
}

/**
 * Desc : This function enables add button when items are selected to their
corresponding select box
*/
function enableDisableAddbtns(elementId) {
	if (null != elementId.id
			&& (elementId.id == "allUsers")) {
		if ($("select[id='allUsers'] option:selected").index() > -1) {
			var reviewProcId = $("#agencyReviewProcess option:selected").val();
			$("#level1Add").removeAttr('disabled');
			$("#level2Add").removeAttr('disabled');
			$("#level3Add").removeAttr('disabled');
			$("#level4Add").removeAttr('disabled');
			if(null != reviewProcId  
					&& (reviewProcId == 3 || reviewProcId == 8))
			{
				$("#level1Add").attr('disabled', 'disabled');
			}
		} 
	}	else if( null != elementId.id
			&& (elementId.id == "level1Add" 
				|| elementId.id == "level2Add"
				|| elementId.id == "level3Add"
				|| elementId.id == "level4Add"	)){
		$("#level1Add").attr('disabled', 'disabled');
		$("#level2Add").attr('disabled', 'disabled');
		$("#level3Add").attr('disabled', 'disabled');
		$("#level4Add").attr('disabled', 'disabled');
	}
}

/**
 *  Desc : This function is called for enabling/disabling go button
 **/
function enableDisableGoBttn(elementId) {
	hideTransactionStatusDiv();
	if (null != elementId.id && elementId.id == "agencyReviewProcess") {
		$("#agencyContainer").hide();
		$("#goBttn").attr('disabled', 'disabled');
		if ($("#agencyReviewProcess").val() != "rps0") {
			$("#goBttn").removeAttr('disabled');
		}
	}
}
var bufferData="";
/**
 *  Desc : This function is called when agency user selects a review process and
 hit 'Go' button
 It makes an ajax call to get level user details and all user details
 */
function ajaxCallForUserAssgnd() {
	hideTransactionStatusDiv();
	pageGreyOut();
	var reviewSelectId = document.getElementById("agencyReviewProcess");
	var reviewProcessId = reviewSelectId.options[reviewSelectId.options.selectedIndex].value;
	var reviewProcessText = reviewSelectId.options[reviewSelectId.options.selectedIndex].text;
	var configFlag = $("#agencyReviewProcess option:selected").attr("labelAttrib");
	var v_parameter = "hdnReviewProcessId=" + reviewProcessId + "&hdnConfigFlag="+ configFlag;
	var urlAppender = $("#hiddenLevelsOfReviewURL").val();
	jQuery
			.ajax({
				type : "POST",
				url : urlAppender,
				data : v_parameter,
				success : function(e) {
					bufferData="";
					bufferData=e;
					removePageGreyOut();
					if (e != null) {
						var msg = e.split("#");
						if (msg[0] == "failure") {
							$("#transactionStatusDiv").html(msg[1]);
							$("#transactionStatusDiv").addClass('failed');
							$("#transactionStatusDiv").show();
						} else {
							document.getElementById('agencyContainer').innerHTML = e;
							$("#reviewProcDesc").html(reviewSelectId.options[reviewSelectId.options.selectedIndex].id);
							enableDisableOnlyLev1(configFlag, reviewProcessId);
							$("#agencyContainer").show();
							$("#levelofRevText").html(reviewProcessText);
						}
					}
				},
				error : function(data, textStatus, errorThrown) {
				}
			});
}

/**
 *  Desc : This function sets visibility of only level 1 -- executed only for
 configuration review tasks*/
function enableDisableOnlyLev1(configFlag, reviewProcessId) {
	if(configFlag == "1"){
		$("#level2To4Div").hide();
	}
	if(reviewProcessId == "3" || reviewProcessId == "8"){
		$("#level1Users").attr('disabled', 'disabled');
	}
}

/** Desc : This function is called when remove button is clicked while removing
 level user (ex. level 1, level 2, level 3, level 4)
 It also sorts level user select box
 Input: buttonId - id of button which is clicked*/
function removeUserFrmLevel(buttonId) {
	hideTransactionStatusDiv();
	var lsLevelUserSelectTag;
	if (null != buttonId.id && buttonId.id == "level1Remove") {
		lsLevelUserSelectTag = document.getElementById('level1Users');
	}
	if (null != buttonId.id && buttonId.id == "level2Remove") {
		lsLevelUserSelectTag = document.getElementById('level2Users');
	}
	if (null != buttonId.id && buttonId.id == "level3Remove") {
		lsLevelUserSelectTag = document.getElementById('level3Users');
	}
	if (null != buttonId.id && buttonId.id == "level4Remove") {
		lsLevelUserSelectTag = document.getElementById('level4Users');
	}

	var lsCounter;
	for (lsCounter = lsLevelUserSelectTag.length - 1; lsCounter >= 0; lsCounter--) {
		if (lsLevelUserSelectTag.options[lsCounter].selected) {
			addToAllAgencyUsers(lsLevelUserSelectTag.options[lsCounter].text,
					lsLevelUserSelectTag.options[lsCounter].value);
			lsLevelUserSelectTag.remove(lsCounter);
		}
	}
	sortlist(lsLevelUserSelectTag);
}

/** Desc : This function is called each time level user is removed,
 It adds removed user to all agency users
 It also sorts All user select box
 Input: text - text to be added to option of all user select tag*/
function addToAllAgencyUsers(text, value) { // lsLevelUserSelectTag
	lsNewTagOption = document.createElement('option');
	lsNewTagOption.value = value;
	lsNewTagOption.title = text;
	lsNewTagOption.text = text;

	lsAllUserSelectTag = document.getElementById('allUsers');
	lsAllUserSelectTag.options.add(lsNewTagOption);
	sortlist(lsAllUserSelectTag);
}

/**
 *  Desc : This function is called when add button is clicked while adding level
 user (ex. level 1, level 2, level 3, level 4)
 It also sort 2 'select' id : 1 is whose button is clicked and 2 is all user
 select box
 It makes a call to enable and disable add button.
 Input: buttonId - id of button which is clicked
 */
function addUserToLevel(buttonId) {
	hideTransactionStatusDiv();
	var lsAllUserSelectTag = document.getElementById('allUsers');
	selectedText = lsAllUserSelectTag.options[lsAllUserSelectTag.selectedIndex].text;
	selectedValue = lsAllUserSelectTag.options[lsAllUserSelectTag.selectedIndex].value;
	selectedTitle = lsAllUserSelectTag.options[lsAllUserSelectTag.selectedIndex].title;

	lsAllUserSelectTag.remove(lsAllUserSelectTag.selectedIndex);

	lsNewTagOption = document.createElement('option');
	lsNewTagOption.value = selectedValue;
	lsNewTagOption.title = selectedTitle;
	lsNewTagOption.text = selectedText;
	var lsLevelUserSelectTag;

	if (null != buttonId.id && buttonId.id == "level1Add") {
		lsLevelUserSelectTag = document.getElementById('level1Users');
	} else if (null != buttonId.id && buttonId.id == "level2Add") {
		lsLevelUserSelectTag = document.getElementById('level2Users');
	} else if (null != buttonId.id && buttonId.id == "level3Add") {
		lsLevelUserSelectTag = document.getElementById('level3Users');
	} else if (null != buttonId.id && buttonId.id == "level4Add") {
		lsLevelUserSelectTag = document.getElementById('level4Users');
	}

	lsLevelUserSelectTag.options.add(lsNewTagOption);

	sortlist(lsLevelUserSelectTag);
	sortlist(lsAllUserSelectTag);
	enableDisableAddbtns(buttonId);
	
}

/**
 *  Desc : This function sorts 'option' of html 'select' tag
 Input: lsSelectTag - id of 'select' tag to be sorted*/
function sortlist(lsSelectTagId) {
	arrTexts = new Array();
	for (i = 0; i < lsSelectTagId.length; i++) {
		arrTexts[i] = lsSelectTagId.options[i].text + '|'
				+ lsSelectTagId.options[i].value;
	}
	arrTexts.sort();

	for (i = 0; i < lsSelectTagId.length; i++) {
		var temp = arrTexts[i].split('|');
		lsSelectTagId.options[i].text = temp[0];
		lsSelectTagId.options[i].value = temp[1];
		lsSelectTagId.options[i].title = temp[0];
	}
}

/**
 *  Below function adds user id separated by delimiter '|''
*/
function setLevel1To4HiddenValues() {
	var lsLevelWiseUsers;

	lsLevelWiseUsers = appendItemsInUrl('allUsers');
	$("#hdnAllUsers").val(lsLevelWiseUsers);
	lsLevelWiseUsers = appendItemsInUrl('level1Users');
	$("#hdnLev1Users").val(lsLevelWiseUsers);

	lsLevelWiseUsers = appendItemsInUrl('level2Users');
	$("#hdnLev2Users").val(lsLevelWiseUsers);

	lsLevelWiseUsers = appendItemsInUrl('level3Users');
	$("#hdnLev3Users").val(lsLevelWiseUsers);

	lsLevelWiseUsers = appendItemsInUrl('level4Users');
	$("#hdnLev4Users").val(lsLevelWiseUsers);
}

/**
 *  Below function adds user id separated by delimiter '|''
*/
function appendItemsInUrl(lsTagId) {
	var lsSelectId = document.getElementById(lsTagId);
	var lsCounter;
	var lsLength;
	var lsValuesToAppend = "";
	if (lsSelectId.length > 0) {
		for (lsCounter = 0; lsCounter < lsSelectId.length; lsCounter++) {
			lsValuesToAppend = lsValuesToAppend
					+ lsSelectId.options[lsCounter].value + "|";
		}
		lsLength = lsValuesToAppend.length;
		lsValuesToAppend = lsValuesToAppend.slice(0, (lsLength - 1));
	}
	return lsValuesToAppend;
}

/**
 *  Desc : This function is called when agency user selects review process -->
 	assigns users level wise and clicks on save button
 It makes an ajax call to save level users in database
 It also displays transaction status at top of page*/
function ajaxCallToSaveLevelUsers() {
	pageGreyOut();
	var reviewProcessId = $("#agencyReviewProcess option:selected").attr("value"); 
	setLevel1To4HiddenValues();
	var v_parameter = "hdnReviewProcId=" + reviewProcessId + "&hdnAllUsers="
			+ $("#hdnAllUsers").val() + "&hdnLev1Users="
			+ $("#hdnLev1Users").val() + "&hdnLev2Users="
			+ $("#hdnLev2Users").val() + "&hdnLev3Users="
			+ $("#hdnLev3Users").val() + "&hdnLev4Users="
			+ $("#hdnLev4Users").val();
	var urlAppender = $("#hiddenSaveLevelUsers").val();

	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			removePageGreyOut();
			bufferData = document.getElementById('agencyContainer').innerHTML;
			$("#saveLevels").attr('disabled', 'disabled');
			if (e != null) {
				var msg = e.split("#");
				if (msg[0] == "success") {
					$("#transactionStatusDiv").html(msg[1]);
					$("#transactionStatusDiv").addClass('passed');
					$("#transactionStatusDiv").show();
				} else if (msg[0] == "failure") {
					$("#transactionStatusDiv").html(msg[1]);
					$("#transactionStatusDiv").addClass('failed');
					$("#transactionStatusDiv").show();
				}
			}
		},
		beforeSend : function() {
		}
	});
}
/**
 * Below function is used to cancel Clicked Level Users.
 * */
function cancelClickedLevelUsers(){
	hideTransactionStatusDiv();
	var reviewSelectId = document.getElementById("agencyReviewProcess");
	var reviewProcessId = reviewSelectId.options[reviewSelectId.options.selectedIndex].value;
	var configFlag = $("#agencyReviewProcess option:selected").attr("labelAttrib");
	var reviewProcessText = reviewSelectId.options[reviewSelectId.options.selectedIndex].text;

	if (bufferData != null) {
		var msg = bufferData.split("#");
		if (msg[0] == "failure") {
			$("#transactionStatusDiv").html(msg[1]);
			$("#transactionStatusDiv").addClass('failed');
			$("#transactionStatusDiv").show();
		} else {
			document.getElementById('agencyContainer').innerHTML = bufferData;
			document.getElementById("levelofRevText").innerHTML = reviewProcessText; 
			$("#reviewProcDesc").html(reviewSelectId.options[reviewSelectId.options.selectedIndex].id);
			enableDisableOnlyLev1(configFlag, reviewProcessId);
			$("#agencyContainer").show();
		}
	}
}

/**
 * Added in R6 for Return Payment.
 * this method is added to enable to disable submit button on bulk notification screen
 * @param elementId
 */
function enableDisableNotificationBttn(elementId) {
	hideTransactionStatusDiv();
	if($('#agencyActionTab').val()!=' ' && $('#agencyProgramName').val()!=' ' && $('#fiscalYearTab').val()!=' '){
		$('#bulkNotificationSubmit').removeAttr('disabled');
	}
	else
		$('#bulkNotificationSubmit').prop('disabled',true);
}

/**
 * Added in R6 for Return Payment.
 * this method will handle the task on bulk notification screen
 * agencyActionTab: it will tell which action to perform i.e export task or sent notification
 */
function submitNotificationForm() {
	pageGreyOut();
	var taskSelected=$('#agencyActionTab').val();
	var v_parameter = "actionSelected=" + $('#agencyActionTab').val() + "&programId="
			+ $("#agencyProgramName option:selected").val() + "&fiscalYear="
			+ $("#fiscalYearTab").val()+ "&programName="
			+ $("#agencyProgramName option:selected").text()+ "&targetUser="+"L2"+"&status=null";
	
	var urlAppender = $("#sendNotificationAlert").val();
		$('#bulkNotificationSubmit').prop('disabled',true);
		$('#submitBulk').prop('disabled',true);
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			if (e != null) {
				$(".overlay").closeOverlay();
				if(taskSelected=="sendNotification"){
				$("#transactionStatusDiv").html('Request received successfully. Notifications will be sent shortly to providers with unrecouped Advances.');
				}
				else{
					$("#transactionStatusDiv").html('Request received succesfully. An e-mail will be sent to you shortly with instructions to download the export file.');
				}
				$("#transactionStatusDiv").addClass('passed');
				$("#transactionStatusDiv").show();
				removePageGreyOut();
				clearNotificationForm();
			}
		},
		error : function() {
			$(".overlay").closeOverlay();
			removePageGreyOut();
			clearNotificationForm();
		}
	});
}

/**
 * Added in R6 for Return Payment.
 * This method will launch confirmation overlay when user select 'send bulk notification' action
 */
function launchSubmitNotificationForm() {
	if($('#agencyActionTab').val()=="sendNotification"){
	$(".overlay").launchOverlay($(".alert_bulk_notification"), $(".exit-panel"), "420px", null, "onReady");
	$('#submitBulk').prop('disabled',false);
	}
	else{
		submitNotificationForm();
		$('#submitBulk').prop('disabled',false);
	}
	
}
/**
 * Added in R6 for Return Payment.
 * This method will clear the bulk Notification form
 */
function clearNotificationForm(){
	$('#notificationScreen').find('select').val('');
}

/**
 * Added in R6 for Return Payment.
 * This function is added to add properties for a div
 * for sample notification in Bulk Notification screen.
 */
function viewBulkNotificationSampleNotify(){
	$('#bulkNotificationDiv').css("display","block");
	$('#bulkNotificationDiv').css("visibility","inherit");	
	
}
/**
 * Added in R6 for Return Payment.
 * This function is added to add properties for a div 
 * for sample notification in Bulk Notification.
 */
function viewBulkNotificationInformation(){
	$('#bulkNotificationInformationDiv').css("display","block");
	$('#bulkNotificationInformationDiv').css("visibility","inherit");	
	
}
//Fix for QC defect : 8611, to show 'X' on sample notification pop up
function closebulkNotificationDiv()
{
	$('#bulkNotificationDiv').hide();
}