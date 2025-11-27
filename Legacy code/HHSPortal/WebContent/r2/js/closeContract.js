$(document).ready(function() {
	$("#usernameDiv").hide();
	$("#passwordDiv").hide();
	$("#errorMsg").hide();
	$("#closeCommentDiv").hide();
	$("#errorGlobalMsg").hide();
	$("#errorGlobalMsg").hide();
	$('#btnCloseContract').attr('disabled','disabled');

	$("#btnCancelCloseContract").click(function() {
		clearAndCloseOverLay();
	});
	$("#btnCloseContract").click(function() {
			submitCloseContract();
	});
	$("#chkCloseContract").click(function() {
		enableDisableUsernamePassword();
	});
	
});

function submitCloseContractForm() {
	var v_parameter = "&" + $("#closeContractForm").serialize();

	var urlAppender = $("#closeContractUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(result) {
			if (result["error"] == 0) {
				clearAndCloseOverLay();
				window.location.href = $("#duplicateRender").val();
			} else if (result["error"] == 1)// For authorization failure
			{
				$("#errorMsg").show();
				$("#errorMsg").html(result["message"]);
			} else if (result["error"] == 2) {
				$("#errorGlobalMsg").show();
				$("#errorGlobalMsg").html(result["message"]);
				clearAndCloseOverLay();
			}
		},
		error : function(data, textStatus, errorThrown) {
			$("#errorGlobalMsg").show();
			$("#errorGlobalMsg").html(textStatus);
			clearAndCloseOverLay();
		}
	});

}

function clearAllErrorMsgs() {
	$("#txtCloseContractUserName").parent().next().html("");
	$("#txtCloseContractPassword").parent().next().html("");
	$("#closeContractComment").next().html("");
	$("#chkCloseContract").parent().next().html("");

	$("#errorMsg").html("");
	$("#errorGlobalMsg").html("");
	$("#successGlobalMsg").html("");	

	$("#errorMsg").hide();
	$("#errorGlobalMsg").hide();
	$("#errorGlobalMsg").hide();	
}

function validateCloseContractForm() {
	tmpError = false;
	if ($("#txtCloseContractUserName").val() == "") {
		$("#txtCloseContractUserName").parent().next().html(
				"! This field is required.");
		tmpError = true;
	}

	if ($("#txtCloseContractPassword").val() == "") {
		$("#txtCloseContractPassword").parent().next().html(
				"! This field is required.");
		tmpError = true;
	}
	if ($("#closeContractComment").val() == "") {
		$("#closeContractComment").next().html(
				"! This field is required.");
		tmpError = true;
	}
	
	return tmpError ? false : true;
}

function submitCloseContract() {
	clearAllErrorMsgs();

	if ($("#chkCloseContract").attr("checked") != "checked") {
		$("#chkCloseContract").parent().next().html(
				"! This field is required.");
	} else if (validateCloseContractForm()) {
	 if(validateTextArea('closeContractComment')){
		submitCloseContractForm();
		return true;
	}else{
		$("#errorMsg").html(invalidResponseMsg);
		$("#errorMsg").show();
		 return false;
	}
	}
	return false;
}

function enableDisableUsernamePassword() {
	clearAllErrorMsgs();
	$("#chkCloseContract").each(function() {
		if ($(this).attr("checked") == 'checked') {
			$("#usernameDiv").show();
			$("#passwordDiv").show();
			$("#closeCommentDiv").show();
			$("#txtCloseContractUserName").removeAttr("disabled");
			$("#txtCloseContractPassword").removeAttr("disabled");
			$("#closeContractComment").removeAttr("disabled");
			$('#btnCloseContract').attr('disabled',false);
		} else {
			$("#txtCloseContractUserName").attr('value', "");
			$("#txtCloseContractPassword").attr('value', "");
			$("#closeContractComment").attr('value', "");

			$("#txtCloseContractUserName").attr("disabled", "disabled");
			$("#txtCloseContractPassword").attr("disabled", "disabled");
			$("#closeContractComment").attr("disabled", "disabled");
			$('#btnCloseContract').attr('disabled','disabled');

			$("#usernameDiv").hide();
			$("#passwordDiv").hide();
			$("#closeCommentDiv").hide();

			clearAllErrorMsgs();
		}
	});
}

/**
 * Function sets the maximum length of Textarea.
 * updated in R5
 */
function setMaxLength(obj,maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}