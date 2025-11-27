/**
 * ==============================================
 * This file contains method that will
 * handle document related task on Award Screen.
 * ==============================================
 */
var configAwardDocFormAction;
/**
 *  on page load
*	Updated Method in R4
**/
$(document)
		.ready(
				function() {
					configAwardDocFormAction = document.configureAwardDocForm.action;
					$('#contractTabs li')
							.removeClass(
									'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
					$("#contractTabs").tabs();
					$("#reassignButton").attr("disabled", "true");
					$("#reassignButton").attr("class", "graybtutton");
					var flag = false;
					$(".reqDocTypeDrpDwn option:selected").each(function() {
						if ($(this).html() != "") {
							flag = true;
						}
					});
					if (flag) {
						$("#finishButton").attr("class", "button");
						$("#finishButton").removeAttr("disabled");
					} else {
						$("#finishButton").attr("class", "graybtutton");
						$("#finishButton").attr("disabled", "true");
					}
					// This function is used to remove document based on
					// document Id
					$('#deleteDoc').click(
							function() {
								pageGreyOut();
								deleteDocument();
							});
					removePageGreyOut();
					// This function is used to enable disable finish button
					// based on required
					// document types
					// selected from drop down
					$(".proposalConfigDrpdwn").change(function() {
						var flag = false;
						$(".reqDocTypeDrpDwn option:selected").each(function() {
							if ($(this).html() != "") {
								flag = true;
							}
						});
						if (flag) {
							$("#finishButton").attr("class", "button");
							$("#finishButton").removeAttr("disabled");
						} else {
							$("#finishButton").attr("class", "graybtutton");
							$("#finishButton").attr("disabled", "true");
						}
					});
					// disable all fields if user logged is different from task
					// assigned to
					var screenReadOnly = $("#screenReadOnly").val();
					if (screenReadOnly == 'true') {
						$("#finishButton").attr("class", "graybtutton");
						$("#finishButton").attr("disabled", "true");
						$("#uploadDoc").attr("class", "graybtutton");
						$("#uploadDoc").attr("disabled", "true");
						$("#addDocument").attr("class", "graybtutton");
						$("#addDocument").attr("disabled", "true");
						$(".proposalConfigDrpdwn").attr("disabled", "true");
						$(".selectbox").attr("disabled", "true");
						//added below line for defect #6363
						$("#chkForDefaultConfigurations").attr("disabled", "true");
					}
					
					
					
				});

/**
 *  This function is used to view procurement summary for agency/provider users
 *  */
function viewProcurementSummary() {
	var url = $("#procurementSummaryURL").val()+"&action=procurementHandler&overlay=true";
	window.open(url,
			'windowOpenTab',
	'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}



/**
 *  This function is used to perform different actions from drop down
*	Updated Method in R4
**/
function actionDropDownForDocuments(documentId, selectElement, docSeqID,docType,  documentName) {
	//Added for R5- combo box for DocType - fix for Defect id 7270
	$('#docTypeHidden').val(docType);
	//R5 end
	$("#removeMessageDiv").hide();
	var value = selectElement.selectedIndex;
	if (value == 1) {
		viewRFPDocument(documentId, documentName);
		selectElement.selectedIndex = "";
	} else if (value == 2) {
		viewDocumentInfo(documentId);
		selectElement.selectedIndex = "";
	} else if (value == 3) {
		removeDocument(documentId,docSeqID);
		selectElement.selectedIndex = "";
	}
}
/**
 *  This function is used to view document information for selected document
 *  */
function viewDocumentInfo(documentId) {
	$("#documentId").val(documentId);
	$("#orgType").val('agency_org');
	$("#configureAwardDocForm")
			.attr("action", $("#configureAwardDocUrl").val());
	$("#submit_action").val("viewDocumentInfo");
	var options = {
		success : function(responseText, statusText, xhr) {
			var $response = $(responseText);
			var data = $response.contents().find(".overlaycontent");
			$("#viewDocumentProperties").html(data.detach());
			$(".overlay").launchOverlay($(".alert-box-viewDocumentProperties"),
					$(".exit-panel.upload-exit"), "600px", null, "onReady");
			var a=$('.documentLocationPath').text().trim();
			a=a.replace(/\\/g, "&#x200b;\\&#x200b;");
			b='<div style="width:50ch;" ></div>';
			$('.documentLocationPath').html(b);
			$('.documentLocationPath div').html(a);
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
	$(configureAwardDocForm).ajaxSubmit(options);
}

/**
 *  This function is used to upload document by clicking Upload Document button
 *	or by selecting Upload Document option from action drop down
 **/
function uploadDocument() {
	pageGreyOut();
	document.configureAwardDocForm.action = $("#uploadAwardDocumentAction")
			.val();
	var options = {
		success : function(responseText, statusText, xhr) {
			var $response = $(responseText);
			var data = $response.contents().find(".overlaycontent");
			$("#tab1").empty();
			$("#tab2").empty();
			// Start Added in R5
			$("#tabnew").empty();
			// End Added in R5
			if (data != null || data != '') {
				$("#tab1").html(data.detach());
			}
			$("#overlayedJSPContent").html($response);
			$(".overlay").launchOverlay($(".alert-box-upload"),
							// Start Updated in R5
					$(".exit-panel.upload-exit"), "850px", null, "onReady");
							// End Updated in R5
			$('ul')
					.removeClass(
							'ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
			$('li')
					.removeClass(
							'ui-state-default ui-corner-top ui-state-hover');
			// Start Added in R5
			$('#step1').removeClass().addClass('active').css({'margin-left':''});
			$('#step2').removeClass().css({'padding':'0px 30px'});
			$('#step3').removeClass().addClass('last').css({'margin-left':''});
			// End Added in R5
			removePageGreyOut();
		},
		error : function(xhr, ajaxOptions, thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	$(document.configureAwardDocForm).ajaxSubmit(options);
}

/**
 *  This function is used to launch overlay for remove document
*	Updated Method in R4
**/
function removeDocument(documentId,docSeqID) {
	pageGreyOut();
	$("#documentId").val(documentId);
	$("#docSeqID").val(docSeqID);
	$(".overlay").launchOverlay($(".alert-box-delete"), $(".exit-panel"),
			"350px", null, "onReady");
	
	removePageGreyOut();
	return false;
}

/**
 *  This function is used to enable disable reassign button based on user
 *	selected from drop down
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
	$("#configureAwardDocForm").attr(
			"action",
			configAwardDocFormAction
					+ "&submit_action=reassignConfigureAwardTask");
	document.configureAwardDocForm.submit();
}

/**
 *  This function is used to finish Configure Award Document task
 *  */
function finishTask() {
	var rowCount = $('#attachDocTable tr').length;
	if (rowCount <= 1) {
		$("#messagediv").text(
				"At least 1 document must be uploaded to finish this task.");
		$("#messagediv").show();
	} else {
		$("#configureAwardDocForm").attr(
				"action",
				configAwardDocFormAction + "&submit_action=finishAwardDocumentTask");
		pageGreyOut();
		document.configureAwardDocForm.submit();
	}
}
/**
 *  This function is used to return to task inbox or task management screen from
 *	which the user comes in
 **/
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
	$("#configureAwardDocForm").attr(
			"action",
			$("#confAwardDocRenderUrl").val() + returnVal
					+ '&taskUnlock=' + taskId);
	document.configureAwardDocForm.submit();
}

/** 
 * This method launches the overlay for adding a document 
Updated Method in R4
*/
function addDocumentToVault(procurementId,workflowId,evaluationPoolMappingId) {
	pageGreyOut();
    var url = $("#hiddenaddDocumentFromVaultUrl").val();
    // Start Updated in R5
    var v_parameter = "procurementId=" + procurementId+ '&workflowId=' + workflowId + '&evaluationPoolMappingId=' + evaluationPoolMappingId+"&selectVault=true&selectAll=true";
    // End Updated in R5
    jQuery.ajax({
		url : url,
		data : v_parameter,
		type : "POST",
		success : function(response) {
			if(response != null || response != ''){
		    	$("#addDocumentFromVault").html(response);
			}
			// Start Updated in R5
			$(".overlay").launchOverlay($(".alert-box-addDocumentFromVault"), $(".exit-panel.upload-exit"), "850px", "650px", "onReady");
			// End Updated in R5
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 *  This function is used to upload document by clicking Upload Document button
*or by selecting Upload Document option from action drop down
*New Method in R4
*/
function deleteDocument() {
	 var url = $("#deleteDocument").val();
	 jQuery.ajax({
			url : url,
			type : "POST",
		success : function(response) {
			$("#removeMessageDiv").show();
			hhsAjaxRender(null, document.configureAwardDocForm,'attachDocuments',url,"setErrorMessage");
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 *  This method closes the overlay.
*	New Method in R4
*/
function setErrorMessage(){
	removePageGreyOut();
	$(".overlay").closeOverlay();
}

/**
 * This will get the Document type list for selected Document category
 * */
function getDocumentTypeList(category, userOrg) {
	pageGreyOut();
	var url = $("#contextPathSession").val()+"/GetContent.jsp?selectedInput=" + category
			+ "&organizationId=" + userOrg;
	postTypeRequest(url);
	removePageGreyOut();
}

/**
 *  This will process the document category request and will get the Document type response through servlet call*/
function postTypeRequest(strURL) {
	var xmlHttp;
	if (window.XMLHttpRequest) { 
		var xmlHttp = new XMLHttpRequest();
	} else if (window.ActiveXObject) { 
		var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlHttp.open('POST', strURL, true);
	xmlHttp.setRequestHeader('Content-Type',
			'application/x-www-form-urlencoded');
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.readyState == 4) {
			updatetypepage(xmlHttp.responseText);
		}
	};
	xmlHttp.send(strURL);
}
