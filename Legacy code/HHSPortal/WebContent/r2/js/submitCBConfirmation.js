/**
 * Javascript file for Submit overlay clicked from Contract Budget screen
 */

// On ready functions

$(document).ready(function() {
	$("#usernameDiv").hide();
	$("#passwordDiv").hide();
	$("#errorMsg").hide();

	// On click of DO NOT submit button on overlay
	$("#btnNotSubmitCB").click(function() {
		clearAndCloseOverLay();
	});
	
	// On click of submit button on overlay
	$("#btnSubmitCB").click(function(){
		submitContractBudget();
	});
	
	// On checking the checkbox
	$("#chkSubmitCBForm").click(function() {
		enableDisableUsernamePassword();
	});	
	$("#btnSubmitCB").attr("disabled", "disabled");
});

//Closes the overlay
function clearAndCloseOverLay() {
	$("#overlayDivId").html("");
	$(".overlay").closeOverlay();
}

//Enables Username and Password fields
function enableDisableUsernamePassword() {
	
	$("#chkSubmitCBForm").each(function() {
		if ($(this).attr("checked") == 'checked') {
			$("#usernameDiv").show();
			$("#passwordDiv").show();
			$("#txtSubmitCBUserName").removeAttr("disabled");
			$("#txtSubmitCBPassword").removeAttr("disabled");
			$("#btnSubmitCB").removeAttr("disabled");
		} else {
			$("#txtSubmitCBUserName").attr('value', "");
			$("#txtSubmitCBPassword").attr('value', "");

			$("#txtSubmitCBUserName").attr("disabled", "disabled");
			$("#txtSubmitCBPassword").attr("disabled", "disabled");

			$("#btnSubmitCB").attr("disabled", "disabled");

			$("#usernameDiv").hide();
			$("#passwordDiv").hide();	
			
		}
	});
}

// On click of Yes, Submit button -- Validates review levels set, launch workflow, save comments &  Budget status changes
function submitContractBudgetConfirmation() {
	
	var v_parameter = "&" + $("#submitCBForm").serialize();
	var urlAppender = $("#validateUser").val();
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(result) {
			clearAllErrorMsgs();
			if (result["error"] == 1)// For authorization failure
			{
				$("#errorMsg").show();
				$("#errorMsg").html(result["message"]);
				removePageGreyOut();
			} else if(result["error"] == 2) // Application exception
			{
				$("#errorMsg").show();
				$("#errorMsg").html(result["message"]);
				removePageGreyOut();
			} else {
				document.submitCBForm.action = $("#submitConfirmation").val()+"&publicCommentArea="+convertSpecialChar($("#publicCommentArea").val()) + "&currentProcurementId="+$('#currentProcurementId').val();
				document.submitCBForm.submit();
			}
		}
	});
	
}


// This method inserts Input values
function insertInput(id,value){
    var para, hiddenInput;
    para = document.getElementById('hiddenTextAreaPara');
    hiddenTextArea = document.createElement('textarea');
    hiddenTextArea.id = id;
    hiddenTextArea.name = id;
    hiddenTextArea.innerHTML = value;
    hiddenTextArea.style.display = "none";
    para.appendChild(hiddenTextArea);
    return false;
}



// To clear the error or success messages shown
function clearAllErrorMsgs() {
	$("#txtSubmitCBUserName").parent().next().html("");
	$("#txtSubmitCBPassword").parent().next().html("");
	$("#chkSubmitCBForm").parent().next().html("");

	$("#errorMsg").html("");
	$("#errorGlobalMsg").html("");
	$("#successGlobalMsg").html("");

	$("#errorMsg").hide();
	$("#errorGlobalMsg").hide();
	$("#successGlobalMsg").hide();
}

// Front end validations to check if username & Password entered
function validateUpdateContractConfigForm() {
	tmpError = false;

	if ($("#txtSubmitCBUserName").val() == "") {
		$("#txtSubmitCBUserName").parent().next().html(
				"! This field is required.");
		tmpError = true;
	}

	if ($("#txtSubmitCBPassword").val() == "") {
		$("#txtSubmitCBPassword").parent().next().html(
				"! This field is required.");
		tmpError = true;
	}
	return tmpError ? false : true;
}

// Call to submit contract budget
function submitContractBudget() {
	
	if ($("#chkSubmitCBForm").attr("checked") != "checked") {
		$("#chkSubmitCBForm").parent().next().html(
				"! This field is required.");
	} else if (validateUpdateContractConfigForm()) {
		submitContractBudgetConfirmation();
		return true;
	}
	return false;
}