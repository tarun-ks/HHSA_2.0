// This method called on load of overlay
$(document).ready(function() {
	$("#usernameDiv").hide();
	$("#passwordDiv").hide();
	$("#errorMsg").hide();

	$("#btnNotSubmit").click(function() {
		clearAndCloseOverLay();
	});
	
	$("#chkSubmitMRForm").click(function() {
		enableDisableUsernamePassword();
	});

	$("#btnMarkAsRegSubmit").attr("disabled", "disabled");
	
});
// This method closes the overlay
function clearAndCloseOverLay() {
	$("#overlayDivId").html("");
	$(".overlay").closeOverlay();
}

// This method enables or disables user name or password on click of check box
function enableDisableUsernamePassword() {
	
	$("#chkSubmitMRForm").each(function() {
		if ($(this).attr("checked") == 'checked') {
			$("#usernameDiv").show();
			$("#passwordDiv").show();
			$("#txtSubmitMRUserName").removeAttr("disabled");
			$("#txtSubmitMRPassword").removeAttr("disabled");
			$("#btnMarkAsRegSubmit").removeAttr("disabled");
		} else {
			$("#txtSubmitMRUserName").attr('value', "");
			$("#txtSubmitMRPassword").attr('value', "");

			$("#txtSubmitMRUserName").attr("disabled", "disabled");
			$("#txtSubmitMRPassword").attr("disabled", "disabled");

			$("#btnMarkAsRegSubmit").attr("disabled", "disabled");

			$("#usernameDiv").hide();
			$("#passwordDiv").hide();
			
			
		}
	});
}
// This method submits the overlay form
function markAmendmentAsRegisteredConfirmation() {
	var v_parameter = "&" + $("#submitMRForm").serialize();
	var urlAppender = $("#markAsRegisteredUrl").val();
	var new_url=$("#duplicateRenderAmendment").val();
	var url_amendment=$("#viewAmendmentsList").val();
	var baseContractId=$('#baseContractId').val();
	$('#transactionStatusDiv').html('');
	//End:defect 8769
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
			} else if (result["error"] == 0) {
				clearAndCloseOverLay();
				//defect 8769
				if ($('#hdnIsViewAmendment').val() == 'true') {
					window.location.href = url_amendment
							+ "&hdnIsViewAmendment=true" + "&hdncontractId="
							+ baseContractId;
				} else {
					window.location.href = new_url;
				}
				}
				

		}
	});
	
}

// This method clears all the error message shown on overlay 
function clearAllErrorMsgs() {
	$("#txtSubmitMRUserName").parent().next().html("");
	$("#txtSubmitMRPassword").parent().next().html("");
	$("#chkSubmitMRForm").parent().next().html("");

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

	if ($("#txtSubmitMRUserName").val() == "") {
		$("#txtSubmitMRUserName").parent().next().html(
				"! This field is required.");
		tmpError = true;
	}

	if ($("#txtSubmitMRPassword").val() == "") {
		$("#txtSubmitMRPassword").parent().next().html(
				"! This field is required.");
		tmpError = true;
	}
	return tmpError ? false : true;
}
//This method submits the overlay form
function markAmendmentAsRegistered() {
	
	if ($("#chkSubmitMRForm").attr("checked") != "checked") {
		$("#chkSubmitMRForm").parent().next().html(
				"! This field is required.");
	} else if (validateUpdateContractConfigForm()) {
		markAmendmentAsRegisteredConfirmation();
	}
}