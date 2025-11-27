// This method called on load of overlay
$(document).ready(function() {
	$("#usernameDiv").hide();
	$("#passwordDiv").hide();
	$("#errorMsg").hide();

	$("#btnNotSubmitCB").click(function() {
		clearAndCloseOverLay();
	});
	
	$("#btnSubmitCB").click(function(){
		submitContractBudget();
	});
	
	$("#chkSubmitCBForm").click(function() {
		enableDisableUsernamePassword();
	});

	$("#btnSubmitCB").attr("disabled", "disabled");
	
});
// This method closes the overlay
function clearAndCloseOverLay() {
	$("#overlayDivId").html("");
	$(".overlay").closeOverlay();
}

// This method enables or disables user name or password on click of check box
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
// This method submits the overlay form
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
				document.submitCBForm.action = $("#submitConfirmation").val()+"&publicCommentArea="+convertSpecialChar($("#publicCommentArea").val());
				document.submitCBForm.submit();
			}
		}
	});
	
}

// This method clears all the error message shown on overlay 
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

// This method validates User name and Password test box error check
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
//This method submits the overlay form
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