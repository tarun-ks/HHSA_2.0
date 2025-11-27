/**
 * This Js has functions for complete PSR module
 **/
var lastDataArray = new Array();
$(document)
		.ready(
				function() {
					$("#psrform").validate(
							{
								/* Changes for defect 7693 starts*/
								ignore: []
								/* Changes for defect 7693 ends*/
							,
								rules : {
									considerationPrice: {
										required : true,
										maxlength : 1000
									},
									multiYearHumanServContract: {
										required : true,
										maxlength : 1000
									},
									basisContractOut: {
										required : true
									},
									contractTermInfo: {
										required : true
									},
									multiYearHumanServOpt: {
										required : {
											depends: function(element) {
												return !$(element).prop("readonly");
											}
										},
										maxlength : 1000
									},
									anticipateLevelComp: {
										required : true
									},
									renewalOption:{
										required : {
											depends: function(element) { 
												return !$(element).hasClass("ignoreValidate");
													}											
										},
										maxlength : 100
									}
									
								},
								messages : {
									considerationPrice: {
										required : "! This field is required",
										maxlength : "! Input should be less than 1000 characters"
									},
									multiYearHumanServContract: {
										required : "! This field is required",
										maxlength : "! Input should be less than 1000 characters"
									},
									basisContractOut: {
										required : "! This field is required"
									},
									contractTermInfo: {
										required : "! This field is required"
									},
									multiYearHumanServOpt: {
										required : "! This field is required",
										maxlength : "! Input should be less than 1000 characters"
									},
									anticipateLevelComp: {
										required : "! This field is required"
									},
									renewalOption: {
										required : "! This field is required",
										maxlength : "! Input should be less than 100 characters"
									}							
								},
								submitHandler : function(form) {
									$('#ErrorDiv').hide();
									$(".overlay").launchOverlay($(".alert-box-completepsr"), $(".exit-panel"), "600px", null);
								},
								invalidHandler: function(event, validator) {
									$('#topLevelError').text("Please fill out all required fields before clicking the 'Finish Task' button.");
									$('#ErrorDiv').css('display','block');
								},
								errorPlacement : function(error, element) {
									error.appendTo(element.closest("span").parent().find("span.error"));
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
					/*radio button check*/
					var _onLoadRadioButton = $('input[name="contractTermInfo"][checked]').val();
					if (_onLoadRadioButton != '0')
					{
						multiYearChangeButton(_onLoadRadioButton);
					}					
					$('#contractTabs li')
							.removeClass(
									'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
					$("#contractTabs").tabs();
					/* disable reassign button onload of jsp*/
					$("#reassignButton").attr("class", "graybtutton");
					/* show save button onload of jsp*/
					$("#saveDiv").show();
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
					$("#btnSubmitCB").click(function(e){
						this.disabled = true;
						});
					/*disable submit button and hide username/password fields*/
					$("#btnSubmitCB").attr("class", "graybtutton");
					$("#btnSubmitCB").attr("disabled", "true");
					$('#usernameDiv.row').hide();
					$('#passwordDiv.row').hide();
					var screenReadOnly = $("#screenReadOnly").val();
					if (screenReadOnly == 'true') {
						$("#finishButton").attr("class", "graybtutton");
						$("#completePsrButton").attr("disabled", "true");
						$("#saveButton").attr("class", "graybtutton");
						$("#saveButton").attr("disabled", "true");
						$(".selectReturned").attr("disabled", "true");
						$("#providerComments").attr("disabled", "true");
					}
					if($("#isForCityTask").val() == "true")
					{
					document.getElementById("reassignDropDown").disabled = true;
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
										deleteAutoSaveData();
										document.location = $self.attr('href');
										$(this).dialog("close");
									},
									Cancel: function () {
										$(this).dialog("close");
										/*Changes for Defect 6872 starts*/
										removePageGreyOut();
										/*Changes for Defect 6872 ends*/
									}
								},
								close: function (event, ui) {
									$(this).remove();
									removePageGreyOut();
								}
							});
							$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
						}
					});
					
			$('input[name="contractTermInfo"]').on('change', function(e) {
				var _checkBoxVal = $(this).val();
				multiYearChangeButton(_checkBoxVal);
			});
			$('#chkSubmitCBForm').change(function showHideButton(){
				if($('#chkSubmitCBForm').prop('checked')){
					$('#usernameDiv.row').show();
					$('#passwordDiv.row').show();
					$("#btnSubmitCB").attr("class", "button");
					$("#btnSubmitCB").removeAttr("disabled");
				}else{
					$('#usernameDiv.row').hide();
					$('#passwordDiv.row').hide();
					$("#btnSubmitCB").attr("class", "graybtutton");
					$("#btnSubmitCB").attr("disabled", "true");
				}
			});
			
			$(".autoSaveTextArea").keyup(function() {
				setChangeFlag();
			});
			$("#psrform input[type=text]").on("keyup", function(){
				$('#usernamespan').text("");
				$('#btnSubmitCB').attr('disabled',false);
				} );
			$("#psrform input[type=password]").on("keyup", function(){
				$('#passwordspan').text("");
				$('#btnSubmitCB').attr('disabled',false);
				} );
		});

/**
 * change the status of Flag
 **/
function setChangeFlag() {
	commentsChange = true;
}
/**
 * This function is used for multi year change Button
 **/	
function multiYearChangeButton(_checkBoxVal){
	if(_checkBoxVal != 0){
		$('#multiYearHumanServOpt').attr('readonly','readonly');
		$('#multiYearHumanServOpt').val("");
		$('#multiYearHumanServOpt_text').attr('disabled','disabled');
		$('#multiYearHumanServOpt_text').attr('contentEditable', false);
		$('#multiYearHumanServOpt_text').html("");
		$('#showMandatory').hide();
		$('#multiYearHumanServOpt_count').text(1000 - $('#multiYearHumanServOpt_text').text().length);
	}else{
		$('#multiYearHumanServOpt').removeAttr('readonly');
		$('#multiYearHumanServOpt_text').removeAttr('disabled');
		$('#multiYearHumanServOpt_text').attr('contentEditable', true);
		$('#multiYearHumanServOpt_count').text(1000 - $('#multiYearHumanServOpt_text').text().length);
		$('#showMandatory').show();
	}
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
 * this function is used to view document in task frame
 **/
function previewUrl(url, target) {
	clearTimeout(window.ht);
	window.ht = setTimeout(
			function() {
				var div = document.getElementById(target);
				div.innerHTML = '<iframe class="iframeProposal" style="width:100%; height:550px; display:block;"  src="'
						+ url + '" />';
			}, 20);
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
 * This function is used to reassign task to selected user
 **/
function reassignTask() {
	pageGreyOut();
	var reAssignUser = $('#reassignDropDown option:selected').html();
	$("#reassignedToUserName").val(reAssignUser);
	$("#psrform").attr("action", $("#psrReassignURL").val());
	document.psrform.submit();
}
/**
 * This function is used to save internal Psr comments
 **/
function savePsrComments() {
	pageGreyOut();
	/*Start R5: UX module, clean AutoSave Data*/
	deleteAutoSaveData();
	/*End R5: UX module, clean AutoSave Data*/
	document.psrform.action = $("#savePsrComments").val();
	var options = {
   		/*function finds the elements belonging to class overlaycontent*/
			success : function(responseText, statusText, xhr) {
				removePageGreyOut();
				var previousComment = $('#internalComments').val();
				$('#comments').val(previousComment);
				var ignoreForms = ["viewdocform"];
				updateSavedData(ignoreForms);
			},/*this function calls overlaycontent function*/
			error : function(xhr, ajaxOptions, thrownError) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		};
  $(document.psrform).ajaxSubmit(options); 
}
/**
 *  This function is called when finish task button is clicked.
 **/
function finishTask() {
	var isValidUsername = $.trim($('#txtSubmitCBUserName').val());
	var isValidPassword = $.trim($('#txtSubmitCBPassword').val());
	if (isValidUsername == "") {
		$("#usernamespan").text("! This field is required");
	}
	if (isValidPassword == "") {
		$("#passwordspan").text("! This field is required");
	} 
	if(isValidUsername != "" && isValidPassword != "")  {
		$("#psrform").attr("action", $("#finishPSRComplete").val());
		document.psrform.submit();
	}
}
/** 
 * This function is used to cancel or close overlay
 **/
function cancelOverLay() {
	$(".overlay").closeOverlay();
}

/**
 * This function is used to return to task inbox or task management screen from
 * which the user comes in
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

		$("#psrform").attr(
				"action",
				$("#completePSRRenderUrl").val() + returnVal
						+ '&taskUnlock=' + taskId);
		document.psrform.submit();
}

$(window).load(function(){
	var ignoreForms = ["viewdocform"];
	updateSavedData(ignoreForms);
});
/*Method to compare.d*/
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
