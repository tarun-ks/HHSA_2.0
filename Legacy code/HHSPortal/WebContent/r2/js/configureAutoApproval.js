/**
 * This js file is used for configure auto approval screen Javascript added in R7
 */

var formDataChange = false;
var selectedAgencyId = $("select[id='agencySelectBox']>option:selected").val();

function setVisibility(id, visibility) {
	document.getElementById(id).style.display = visibility;
}

/**
 * Desc : This function is called for enabling/disabling of views
 */
function enableDisableId(elementId) {
	// below condition is for agency select box
	if(!(clickOnGridArr.length>0 || commentsChange ||changeOnGridCommentsArr.length>0 || formDataChange)){
		if (null != elementId.id && elementId.id === "agencySelectBox") {
			hideTransactionStatusDiv();
			$("#autoApprovalThresholdDiv").hide();
			$("#goButton").attr('disabled', 'disabled');
			if ($('#goButton').is(':enabled')) {
				$("#goButton").attr('disabled', 'disabled');
			}
	
			if ($("#agencySelectBox").val() !== "Select an Agency") {
				$("#goButton").removeAttr('disabled');
			} else {
				$("#goButton").attr('disabled', 'disabled');
				$("#autoApprovalThresholdDiv").hide();
			}
		}
	}
}

/**
 * Desc : This function is called when city user selects agency 
 *  --> Go button It makes an ajax call to fetch threshold value if
 * exist in database
 */
function ajaxCallForDefaultThreshold() {
	hideTransactionStatusDiv();
	pageGreyOut();
	var agencySelectId = document.getElementById("agencySelectBox");
	var agencyId = agencySelectId.options[agencySelectId.options.selectedIndex].value;
	var v_parameter = "hdnAgencyId=" + agencyId;
	var urlAppender = $("#hiddenFetchAutoApprovalThresholdURL").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$('#autoApprovalThresholdDiv').show();
			$('#autoApprovalThresholdDiv').html(e);
			if($('#thresholdValue').val() == ""){
				$('#thresholdValue').val("0");
			}
			if($('#modified_by').val() == ""){
				$('#modifiedByDiv').hide();
			}
			if($('#modifiedDate').val() == ""){
				$('#modifiedDateDiv').hide();
			}
			removePageGreyOut();
		},
		beforeSend : function() {
		}});
}

/**
 * Desc : This function is called when city user selects agency --> enter updated threshold value and clicks on save button It makes an
 * ajax call to save threshold value in database
 */
function ajaxCallToSaveThreshold() {
	saveMethod();
}

/**
 * This function is used to save details
 */
function saveMethod() {
	var agencySelectId = document.getElementById("agencySelectBox");
	var agencyId = agencySelectId.options[agencySelectId.options.selectedIndex].value;
	var thresholdValue = document.getElementById("thresholdValue").value;
	pageGreyOut();
	$('#autoApprovalThresholdDiv').html("");
	var v_parameter = "hdnAgencyId=" + agencyId + "&hdnThresholdValue=" + thresholdValue;
	var urlAppender = $("#hiddenSaveThresholdValueURL").val();
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			if (e != null) {
				var a=e.split(".")[0];
				var msg = a.split("#");
				if (msg[0] === "success") {
					resetThreshold();
					$("#transactionStatusDiv").html(msg[1]);
					$("#transactionStatusDiv").addClass('passed');
					$("#transactionStatusDiv").show();
				} else if (msg[0] === "failure") {
					$("#transactionStatusDiv").html(msg[1]);
					$("#transactionStatusDiv").addClass('failed');
					$("#transactionStatusDiv").show();
				}
				var len = a.length;
				$('#autoApprovalThresholdDiv').show();
				$('#autoApprovalThresholdDiv').html(e.substr(len+1));
				resetFormDataChange();
				clickOnGridArr.length = 0;
			}
			removePageGreyOut();
		},
		beforeSend : function() {
		}
	});
}

$("#thresholdValue").keypress(function(e) {
    // between 0 and 9
	if ( e.key=="Backspace" ||  e.key=="Delete" || e.key=='ArrowLeft' || e.key=='ArrowRight'  || (e.which >=48 && e.which<= 57))  {
        return true;  // processing
    }
    else{
    	return false; //stop processing
    } 
});
/**
 * Below function hide error status div(this happens when user changes or takes
 * another action)
 */
function hideTransactionStatusDiv() {
	$("#transactionStatusDiv").hide();
	$("#transactionStatusDiv").html("");
	$("#transactionStatusDiv").removeClass('failed');
	$("#transactionStatusDiv").removeClass('passed');
}
function showGridSuccessMessage() {
	var urlAppender = $("#hiddenGetGridOperationMessageURL").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			if (e !== 'null' && e !== '') {
				$("#transactionStatusDiv").html("The auto-approval settings have been successfully updated.");
				$("#transactionStatusDiv").removeClass('failed');
				$("#transactionStatusDiv").addClass('passed');
				$("#transactionStatusDiv").show();
			}
		},
		beforeSend : function() {
		}
	});
}

/**
 * Below function is called when user clicks cancel button
 */
function resetThreshold() {
	hideTransactionStatusDiv();
	$("#goButton").attr('disabled', 'disabled');
}

/**
 * Below function is called to display unsaved data popup on click 
 * of agency drop down
 */
$("#agencySelectBox ").change(function(e) {
	var $self=$(this);
	if(!$(this).hasClass("active")){
		if(clickOnGridArr.length>0 || commentsChange ||changeOnGridCommentsArr.length>0 || formDataChange){
			e.preventDefault();
			e.stopPropagation();
			e.stopImmediatePropagation();
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						pageGreyOut();
						ajaxCallForDefaultThreshold();
						$self.click();
						clickOnGridArr.length = 0;
						$(this).dialog("close");
					},
					Cancel: function () {
						$("#agencySelectBox").val(selectedAgencyId);
						$(this).dialog("close");
					}
				},
				close: function () {
					$(this).remove();
				}
			});
			$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
			return false;
		}
	}
});

function setFormDataChange() {
	formDataChange = true;
}

function resetFormDataChange() {
	formDataChange = false;
}

/**
 * Below function is called to display unsaved data pop up on click 
 * of Go button
 */
$("#goButton ").click(function(e) {
	var $self=$(this);
	if(!$(this).hasClass("active")){
		if(clickOnGridArr.length>0 || commentsChange ||changeOnGridCommentsArr.length>0 || formDataChange){
			e.preventDefault();
			e.stopPropagation();
			e.stopImmediatePropagation();
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						pageGreyOut();
						location.reload(true);
						$self.click();
						$(this).dialog("close");
					},
					Cancel: function () {
						$(this).dialog("close");
					}
				},
				close: function () {
					$(this).remove();
				}
			});
			$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
			return false;
		}
	}
});
