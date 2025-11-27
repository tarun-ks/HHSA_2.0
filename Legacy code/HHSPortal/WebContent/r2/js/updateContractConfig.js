/* This JS file is used specifically on Update ConntactConfiguration Overlay Popup */

// Defining the document's default actions on onLoad
$(document).ready(function() {
	// By default the username and password input blocks would be hidden
	$("#usernameDiv").hide();
	$("#passwordDiv").hide();
	// By default Error Message block would be in hidden state
	$("#errorMsg").hide();

	// Cancel button on click action definition - clear the contents and hide
	// the Overlay
	$("#btnCacelUpdateConfiguration").click(function() {
		clearAndCloseOverLay();
	});

	// Update/Submit button on click action definition
	$("#btnUpdateConfiguration").click(function() {
		submitUpdateContractConfig();
	});

	// Checkbox button on click action definition
	$("#chkUpdateContractConfig").click(function() {
		enableDisableUsernamePassword();
	});

	// By Default all the success and error messages blocks are cleared and
	// hidden
	clearAllErrorMsgs();

});

/*
 * This function will submit the request for updating contract configuration and
 * updates the respective message blocks on overlay/landing page
 */
function submitUpdateContractConfigData() {
	pageGreyOut();
	// Serializse the form to send the form data
	var v_parameter = "&" + $("#updateContractConfigForm").serialize();

	var urlAppender = $("#updateContractConfigurationUrl").val();
	// Define the Ajax call and events
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(result) {
			clearAllErrorMsgs();
			// Check for Success message (error-0)
			if (result["error"] == 0) {
				$("#successGlobalMsg").show();
				$("#successGlobalMsg").html(result["message"]);
				clearAndCloseOverLay();
				window.location.href = $("#duplicateRender").val();
			}
			// Check for Error message to be shown on same Overlay (error-1)
			else if (result["error"] == 1)// For authorization failure
			{
				$("#errorMsg").show();
				$("#errorMsg").html(result["message"]);
			}
			// Check for Error message to be shown on landing page (error-2)
			else if (result["error"] == 2) {
				$("#errorGlobalMsg").show();
				$("#errorGlobalMsg").html(result["message"]);
				clearAndCloseOverLay();
			}
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			$("#errorGlobalMsg").show();
			$("#errorGlobalMsg").html(textStatus);
			clearAndCloseOverLay();
		}
	});

}

/*
 * This function clears the username/password blocks and all the success and
 * error messages blocks and hide them all
 */
function clearAllErrorMsgs() {
	$("#txtUpdateContractConfigUserName").parent().next().html("");
	$("#txtUpdateContractConfigPassword").parent().next().html("");
	$("#chkUpdateContractConfig").parent().next().html("");

	$("#errorMsg").html("");
	$("#errorGlobalMsg").html("");
	$("#successGlobalMsg").html("");

	$("#errorMsg").hide();
	$("#errorGlobalMsg").hide();
	$("#successGlobalMsg").hide();
}

/*
 * This function validates the form for update contract configuration form
 * against vigibility and correct formed data in username and password fields
 */
function validateUpdateContractConfigForm() {
	tmpError = false;

	if ($("#txtUpdateContractConfigUserName").val() == "") {
		$("#txtUpdateContractConfigUserName").parent().next().html(
				"! This field is required.");
		tmpError = true;
	}

	if ($("#txtUpdateContractConfigPassword").val() == "") {
		$("#txtUpdateContractConfigPassword").parent().next().html(
				"! This field is required.");
		tmpError = true;
	}
	return tmpError ? false : true;
}

/*
 * This function validate the inputs and submits the request
 */
function submitUpdateContractConfig() {
	clearAllErrorMsgs();
	if ($("#chkUpdateContractConfig").attr("checked") != "checked") {
		$("#chkUpdateContractConfig").parent().next().html(
				"! This field is required.");
	} else if (validateUpdateContractConfigForm()) {
		submitUpdateContractConfigData();
		return true;
	}
	return false;
}

/*
 * This function is called on onClick(actually on select) of check-box for
 * Update Contract Configuration - Responsible for hiding and showing the
 * username and password blocks
 */
function enableDisableUsernamePassword() {
	clearAllErrorMsgs();
	$("#chkUpdateContractConfig").each(function() {
		if ($(this).attr("checked") == 'checked') {
			$("#usernameDiv").show();
			$("#passwordDiv").show();
			$("#txtUpdateContractConfigUserName").removeAttr("disabled");
			$("#txtUpdateContractConfigPassword").removeAttr("disabled");
			$("#btnUpdateConfiguration").removeAttr("disabled");
		} else {
			$("#txtUpdateContractConfigUserName").attr('value', "");
			$("#txtUpdateContractConfigPassword").attr('value', "");

			$("#txtUpdateContractConfigUserName").attr("disabled", "disabled");
			$("#txtUpdateContractConfigPassword").attr("disabled", "disabled");

			$("#btnUpdateConfiguration").attr("disabled", "disabled");

			$("#usernameDiv").hide();
			$("#passwordDiv").hide();

			clearAllErrorMsgs();
		}
	});
}
/*
 * This method will call two methods 
 * 1.validateUpdateContractConfigForm()
 * 2.submitUpdateContractConfigData()
 */
function enterTabPressed(e) {
if (e.keyCode == 13) {
	validateUpdateContractConfigForm();
	submitUpdateContractConfigData();
}
}

