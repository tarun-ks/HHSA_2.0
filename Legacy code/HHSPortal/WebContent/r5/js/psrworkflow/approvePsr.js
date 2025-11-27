/**
 * This Js has Functions for approve PSR module
 **/
var lastDataArray = new Array();
$(document)
		.ready(
				function() {
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
					/*disable submit button and hide username/password fields*/
					$("#btnSubmitCB").attr("class", "graybtutton");
					$("#btnSubmitCB").attr("disabled", "true");
					$('#usernameDiv.row').hide();
					$('#passwordDiv.row').hide();
					$("#approvePsrButton").attr("disabled", "true");
					var screenReadOnly = $("#screenReadOnly").val();
					if (screenReadOnly == 'true') {
						$("#finishButton").attr("class", "graybtutton");
						$("#finishtaskchild").attr("disabled", "true");
						$("#saveButton").attr("class", "graybtutton");
						$("#saveButton").attr("disabled", "true");
						$(".selectReturned").attr("disabled", "true");
						$("#providerComments").attr("disabled", "true");
					}
					$("#btnSubmitCB").click(function(e){
						this.disabled = true;
						});		
					 /*remove page grey out on load of jsp*/
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
										removePageGreyOut();
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
 * Used to enable disable finish Button
 **/
function enableFinishButton()
{
	if(document.getElementById("finishtaskchild").value ==''){
		$("#approvePsrButton").attr("disabled", "true");
	}
	else
	{
		$("#approvePsrButton").removeAttr("disabled");
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
  * This function is used to enable disable reassign button.
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
	$("#psrform").attr("action", $("#reassignApprovePsrTask").val());
	document.psrform.submit();
}
/**
 * This function is used to save internal comments
 **/
function savePsrComments() {
	pageGreyOut();
	document.psrform.action = $("#savePsrComments").val();
	var options = {
   		/*function finds the elements belonging to class overlaycontent*/
			success : function(responseText, statusText, xhr) {
				removePageGreyOut();
				/*Start of changes for release 5 defect 6851*/
				var ignoreForms = [];
				updateSavedData(ignoreForms);
				/*End of changes for release 5 defect 6851*/
			},/*this function calls overlaycontent function*/
			error : function(xhr, ajaxOptions, thrownError) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		};
  $(document.psrform).ajaxSubmit(options); 
}
/**
 * This function is used to check if Comment is Blank
 **/
function isCommentBlank(){
	var _isBlank = false;
	if($.trim($("#internalComments").val()) == ""){
		_isBlank = true;
	}
	return _isBlank;
}
/**
 * This function is used to check Finish Task
 **/
function checkFinishTask() {
	if ($("#finishtaskchild").val() == "Returned") {
		if (isCommentBlank()) {
			$('#ErrorDiv').html("Comments are required when returning awards.");
			$('#ErrorDiv').show();
			
			 $("#psrform").submit(function(e){
			        e.preventDefault();
			    });
		} else {
			$("#psrform").attr("action", $("#returnApprovePSR").val());
			document.psrform.submit();
			$('#approvePsrButton').prop('disabled', true);
		}
	}
	else if ($("#finishtaskchild").val() == "Approved"){
		$('#ErrorDiv').hide();
		$("#psrform").submit(function(e){
	        e.preventDefault();
	    });
		$(".overlay").launchOverlay($(".alert-box-approvepsr"), $(".exit-panel"), "600px", null);
	}
}
 /**
  * This function is used to finish task
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
		$("#psrform").attr("action", $("#finishApprovePSR").val());
		document.psrform.submit();
	}
}
/**
 * This function is used to close overlay
 **/
function cancelOverLay() {
	$(".overlay").closeOverlay();
}

/**
 * This function is used to return to task inbox or task management screen from
 *  which the user comes in
 **/
function returnToAgencyTaskList(taskId) {
	var returnVal;
	if($("#isForCityTask").val() == "true")
	{
		returnVal="&returninbox=inbox";
	}
	else
	{
		returnVal="&returninbox=inbox";
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
