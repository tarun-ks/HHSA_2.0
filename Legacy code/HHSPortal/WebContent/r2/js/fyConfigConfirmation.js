//This method is invoked on clickin 'Start New FY Configuration' button and launches its overlay
function NewFYConfigBtn() {
	clearAllErrorMsgs();
	pageGreyOut();
	var url = $("#fyConfigConfirmUrl").val();
	jQuery.ajax({
		type : "POST",
		url : url,
		success : function(responseText) {
			clearAllErrorMsgs();
			if (responseText["error"] == 0) {
				clearAndCloseOverLay();
				window.location.href = $("#duplicateRender").val();
			} else if (responseText["error"] == 2) {
				$("#errorMsg").show();
				$("#errorMsg").html(responseText["message"]);
			}
			 removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
// This method clears all the error messages
function clearAllErrorMsgs() {
	$("#errorGlobalMsg").html("");
	$("#successGlobalMsg").html("");
	$("#transactionStatusDiv").html("");

	$("#errorGlobalMsg").hide();
	$("#successGlobalMsg").hide();
	$("#transactionStatusDiv").hide();
}