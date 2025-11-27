// On page load
$(document).ready(function() 
		{
	$("#usernameDiv").hide();
	$("#passwordDiv").hide();
	$("#errorMsg").hide();
	$("#btnSubmitInvoice").attr("disabled", "disabled");

	$("#btnNotSubmitInvoice").click(function() {
		clearAndCloseOverLay();
	});

	$("#btnSubmitInvoice").click(function(){
		submitInvoice();
	});

	$("#chkSubmitInvoiceForm").click(function() {
		enableDisableUsernamePassword();
	});
		});

/*
 * This method closes the pop-up
 */
function clearAndCloseOverLay() {
	$("#overlayDivId").html("");
	$(".overlay").closeOverlay();
}

/*
 * This method is invoked when user checks or unchecks the "I Agree"
 * check-box on Invoice Submission Pop-up
 */
function enableDisableUsernamePassword()
{
	$("#chkSubmitInvoiceForm").each(function() {
		if ($(this).attr("checked") == 'checked') {
			$("#usernameDiv").show();
			$("#passwordDiv").show();
			$("#txtSubmitCBUserName").removeAttr("disabled");
			$("#txtSubmitCBPassword").removeAttr("disabled");
			$("#btnSubmitInvoice").removeAttr("disabled");
		} else {
			$("#txtSubmitCBUserName").attr('value', "");
			$("#txtSubmitCBPassword").attr('value', "");

			$("#txtSubmitCBUserName").attr("disabled", "disabled");
			$("#txtSubmitCBPassword").attr("disabled", "disabled");

			$("#btnSubmitInvoice").attr("disabled", "disabled");

			$("#usernameDiv").hide();
			$("#passwordDiv").hide();

			$("#userNameErrorMsgHolder").hide();
			$("#pwdErrorMsgHolder").hide();
		}
	});
}

/*
 * This method is called when user clicks "Yes" button
 * on Invoice Submission pop-up.
 * 
 * It validates the user that is trying to submit the invoice.
 * 
 * It shows the error message in case unauthorized user try to submit the invoice
 */
function submitInvoiceConfirmation() 
{
	pageGreyOut();
	var v_parameter = "&" + $("#submitInvoiceForm").serialize();
	var urlAppender = $("#validateUser").val();
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
				document.submitInvoiceForm.action = $("#submitConfirmation").val()
				/* Begin REL 3.9.0 QC 6114  Don't send comments as queryString */
	/*			+ "&publicCommentArea="+convertSpecialChar($("#publicCommentArea").val())    */
				/* End REL 3.9.0 QC 6114  Don't send comments as queryString */
				;
				document.submitInvoiceForm.submit();
			}
		}
	});
}

/*
 * This method clears all the top-level error messages.
 */
function clearAllErrorMsgs() 
{
	$("#userNameErrorMsgHolder").hide();
	$("#pwdErrorMsgHolder").hide();

	$("#errorMsg").html("");
	$("#errorMsg").hide();
}

/*
 * It validates the invoice submission form.
 * 
 * Shows the required field validation in case username or password is left blank.
 */
function validateInvoiceSubmitForm() {
	isUsrNameValid = false;
	isPwdNameValid = false;

	if ($("#txtSubmitCBUserName").val() == "") {
		$("#userNameErrorMsgHolder").show();
		$("#userNameErrorMsgHolder").html(
				"! This field is required.");
		isUsrNameValid = false;
	} else{
		$("#userNameErrorMsgHolder").hide();
		isUsrNameValid = true;
	}


	if ($("#txtSubmitCBPassword").val() == "") {
		$("#pwdErrorMsgHolder").show();
		$("#pwdErrorMsgHolder").html(
				"! This field is required.");
		isPwdNameValid = false;
	} else{
		$("#pwdErrorMsgHolder").hide();
		isPwdNameValid = true;
	}
	return (isUsrNameValid && isPwdNameValid);
}

/*
 * It is called when user selects "Yes" button on 
 * Invoice submission confirmation pop-up
 */
function submitInvoice() 
{

	if (validateInvoiceSubmitForm()) {
		submitInvoiceConfirmation();
		return true;
	}
	return false;
}