/**
 * This file contains methods that will handle the task
 * Accept Proposal.
 * This contains method that will handle the form display,
 * view document information.
 */
var lastDataArray = new Array();
$(document)
		.ready(
				function() {
					$('#contractTabs li')
							.removeClass(
									'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
					$("#contractTabs").tabs();
					/* disable reassign button onload of jsp*/
					$("#reassignButton").attr("disabled", "true");
					$("#reassignButton").attr("class", "graybtutton");
					/* show save button onload of jsp*/
					$("#saveDiv").show();
				/*	 check for proposal task status*/
					var proposalTaskStatus = $("#proposalTaskStatus").val();
				/*	 If proposal task status is 'Accepted for Evaluation' or
					 'Returned for Revision' or 'Non-Responsive', enable
					 finish button*/
					if (proposalTaskStatus == 'Accepted for Evaluation'
							|| proposalTaskStatus == 'Returned for Revision'
							|| proposalTaskStatus == 'Non-Responsive') {
						$("#finishButton").attr("class", "button");
						$("#finishButton").removeAttr("disabled");
					} else {
						$("#finishButton").attr("class", "graybtutton");
						$("#finishButton").attr("disabled", "true");
					}
					/* show hide save button on select of contract tabs*/
					$("#contractTabs").find('a').each(function() {
						$(this).click(function() {
							if ($(this).hasClass('showButton')) {
								$("#saveDiv").show();
							} else if ($(this).hasClass('hideButton')) {
								$("#saveDiv").hide();
							}
						});
					});
					/* disable all fields if user logged is different from task
					 assigned to*/
					var screenReadOnly = $("#screenReadOnly").val();
					if (screenReadOnly == 'true') {
						$("#finishButton").attr("class", "graybtutton");
						$("#finishButton").attr("disabled", "true");
						$("#saveButton").attr("class", "graybtutton");
						$("#saveButton").attr("disabled", "true");
						$(".selectReturned").attr("disabled", "true");
						$("#providerComments").attr("disabled", "true");
						$("#internalComments").attr("disabled", "true");
					}
					/* remove page grey out on load of jsp*/
					removePageGreyOut();
					$("a[id!='smallA'][id!='mediumA'][id!='largeA']").click(function(e) {
						var $self=$(this);
						var isSame = true;
						if(lastDataArray != null && lastDataArray.length > 0){
							$.each(lastDataArray, function(i) {
								if(!$(lastDataArray[i][1]).compareLocal($("form[name='"+lastDataArray[i][0]+"']").serializeArray())){
									isSame = false;
								}
							});
						}
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
				});

/** 
 * This function is used to view procurement summary for agency/provider users
 * */
function viewProcurementSummary() {
	var url = $("#procurementSummaryURL").val()+"&action=procurementHandler&overlay=true";
	window.open(url,
			'windowOpenTab',
	'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}


/** 
 * this function is used to view document in task frame
 * */
function previewUrl(url, target) {
	clearTimeout(window.ht);
	window.ht = setTimeout(
			function() {
				var div = document.getElementById(target);                          // fix for defect-6929
				div.innerHTML = '<iframe class="iframeProposal" style="width:100%; height:680px; display:block;"  src="'
						+ url + '" />';
			}, 20);
}

/** 
 * this function is used to view document properties for selected document
Fix for 8229 
*/
function viewDocumentInfo(documentId,documentType,currentObj) {
	$("#linkDiv").attr("style","display:none");
	var orgType;
	var divId = $(currentObj).closest("div").parent().attr("id");
	/* set orgtype as provider_org for Proposal Documents*/
	if (divId == 'ProposalDocuments') {
		orgType = 'provider_org';
		
	}
	/* set orgtype as city_org for RFP Documents*/
	if (divId == 'RFPDocuments') {
		orgType = 'city_org';
	}
	$("#hiddenDocumentId").val(documentId);
	$("#docTypeHidden").val(documentType);
	$("#orgType").val(orgType);
/*	 Start || Modified as a part of Enhancement #5688 for Release 3.2.0*/
	if($("#isForCityTask").val() == "true")
	{ 
		$("#controller_action").val("agencyWorkflowCity");
	}
	/* End || Modified as a part of Enhancement #5688 for Release 3.2.0*/
	$("#acceptProposalForm").attr("action", $("#acceptProposalUrl").val());
	$("#submit_action").val("viewDocumentInfo");
	/* create options for ajax submit*/
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
	/* submit ajax form*/
	$("#acceptProposalForm").ajaxSubmit(options);
}

/** 
 * This function is used to view proposal summary for selected proposal
 * */
function viewProposalSummary(proposalId, procurementId) {
/*	 Start || Modified as a part of Enhancement #5688 for Release 3.2.0*/
	var returnVal;
	if($("#isForCityTask").val() == "true")
	{
		returnVal="&controller_action=agencyWorkflowCity";
	}
	else
	{
		returnVal="";
	}
	/* End || Modified as a part of Enhancement #5688 for Release 3.2.0*/
	removeTaskSelectedClass();
	document.getElementById(proposalId).className = "taskSelected";
	previewUrl($("#viewProposalSummaryUrl").val()
			+ "&jspPath=procurement/evaluation/&removeMenu=asdas" + returnVal, 'linkDiv');
}

/** 
 * this function is used to save accept proposal task details
 * */
function saveAcceptProposalTaskDetails() {
	/*Start R5: UX module, clean AutoSave Data*/
	deleteAutoSaveData();
	/*End R5: UX module, clean AutoSave Data*/
	$("#messagediv").hide();
	var returnedValue = false;
	var nonResponsiveValue = false;
	/* check if proposal details and documents have both Returned and
	 Non-Responsive status*/
	$("#ProposalDocuments").find("select").each(
			function(i) {
				var selectBoxValue = $(
						'#' + $(this).attr("id") + ' option:selected').html();
				if (selectBoxValue == 'Returned') {
					returnedValue = true;
				} else if (selectBoxValue == 'Non-Responsive') {
					nonResponsiveValue = true;
				}
			});
	/* If status is both Returned and Non-Responsive, display error message*/
	if (returnedValue && nonResponsiveValue) {
		$("#messagediv")
				.text(
						"You cannot mark documents both 'Returned' and 'Non-Responsive' within the same task");
		$("#messagediv").show();
	}
	/* otherwise, submit form to save accept proposal task details*/
	else {
		if(validateTaskComment("providerComments", "internalComments")){
			$("#acceptProposalForm").attr(
					"action",
					$("#acceptProposalForm").attr("action")
							+ "&submit_action=saveAcceptProposalTaskDetails");
			var ignoreForms = ["viewdocform"];
			updateSavedData(ignoreForms);
			pageGreyOut();
			document.acceptProposalForm.submit();
		} else {
			removePageGreyOut();
			$("#ErrorDiv").html(invalidResponseMsg);
			$("#ErrorDiv").show();
		}
		
	}
}

/** 
 * This function is used to enable disable reassign button
 * */
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
 * */
function reassignTask() {
	pageGreyOut();
	var reAssignUser = $('#reassignDropDown option:selected').html();
	$("#reassignedToUserName").val(reAssignUser);
	$("#acceptProposalForm").attr(
			"action",
			$("#acceptProposalForm").attr("action")
					+ "&submit_action=reassignAcceptProposalTask");
	pageGreyOut();
	document.acceptProposalForm.submit();
}

/** 
 * This function is used to finish task
 * */
function finishTask() {
	var proposalTaskStatus = $("#proposalTaskStatus").val();
	var providerComments = $("#providerComments").val();
	/* check for provider comments if status is 'Returned for Revision' or
	 'Non-Responsive'*/
	if ((proposalTaskStatus == 'Returned for Revision' || proposalTaskStatus == 'Non-Responsive')
			&& (providerComments == null || providerComments == '')) {
		$("#messagediv").text(
				"Comments must be entered in the Provider comments box.");
		$("#messagediv").show();
	}
/*	 if proposal task status is 'Non-Responsive', launch overlay mark non
	 Responsive*/
	else if (proposalTaskStatus == 'Non-Responsive') {
		pageGreyOut();
		$(".overlay").launchOverlay($(".alert-box-markNonResponsive"),
				$(".exit-panel.upload-exit"), "600px", null);
		removePageGreyOut();
	}
	/* else submit form to finish task*/
	else {
		if(validateTaskComment("providerComments", "internalComments")){
			$("#acceptProposalForm").attr(
					"action",
					$("#acceptProposalForm").attr("action")
							+ "&submit_action=finishAcceptProposalTask");
			pageGreyOut();
			document.acceptProposalForm.submit();
		} else {
			removePageGreyOut();
			$("#ErrorDiv").html(invalidResponseMsg);
			$("#ErrorDiv").show();
		}
	}
}

/** This function is used to close overlay*/
function cancelOverLay() {
	$(".overlay").closeOverlay();
}

/** 
 * This function will close view document info overlay
 * */
function closeOverLayInfo(){
	$("#linkDiv").attr("style","display:block");
}
/** 
 * This function is used to finish task when proposal task status is
 "Non-Responsive"
 */
function markNonResponsive() {
	$("#submit_action").val("markProposalNonResponsive");
	var options = {
		success : function(responseText, statusText, xhr) {
			pageGreyOut();
			$("#overlay").closeOverlay();
			window.location.href = $("#contextPathSession").val()
					+ "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&returnToAgencyTask=true";
		},
		error : function(xhr, ajaxOptions, thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	pageGreyOut();
	$("#acceptProposalForm").ajaxSubmit(options);
}

/** 
 * This function is used to disable button on changing assign status value
 * */
function disableFinishButtonOnChange(currentObj) {
	var selectBoxValue = $('#' + $(currentObj).attr("id") + ' option:selected')
			.html();
	var prevSelectedVal = $(currentObj).next().val();
	if (selectBoxValue == prevSelectedVal) {
		$("#finishButton").attr("class", "button");
		$("#finishButton").removeAttr("disabled");
	} else {
		$("#finishButton").attr("class", "graybtutton");
		$("#finishButton").attr("disabled", "true");
	}
}

/**
 *  This function will remove task selected class from other proposal links
 *  */
function removeTaskSelectedClass() {
	var arrowDiv = document.getElementsByName("taskArrow");
	for ( var i = 0; i < arrowDiv.length; i++) {
		arrowDiv[i].className = "taskNormal";
	}
}

/**
 * This function is used to return to task inbox or task management screen from
which the user comes in
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

		$("#acceptProposalForm").attr(
				"action",
				$("#acceptProposalRenderUrl").val() + returnVal
						+ '&taskUnlock=' + taskId);
		document.acceptProposalForm.submit();
}

$(window).load(function(){
	var ignoreForms = ["viewdocform"];
	updateSavedData(ignoreForms);
});

/**
 * Method to compare.d
 * */
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