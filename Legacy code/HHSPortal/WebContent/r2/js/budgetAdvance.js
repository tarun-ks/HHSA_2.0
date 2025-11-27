//This js file is used for the page budgetAdvance.jsp

function submitMyForm() {
	pageGreyOut();
	var v_parameter =  "&" + $("#requestAdvance").serialize();	
	var urlAppender = $("#hdnRequestAdvanceUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(responseText) {
			if(responseText!=null && ""!=responseText){
				$("#ErrorDiv").html(responseText);
				$("#ErrorDiv").show();
				removePageGreyOut();
			}else {
				$("#successMsgBudgetList").show();
				$("#successMsgBudgetList").html(advanceSubmitted);
				clearAndCloseOverLay();
				removePageGreyOut();
			}
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
	
}

// This method is used for validation of the fields of the form
function validateForm() {
	tmpError = false;
	if(parseFloat($("#advAmntRequested").val()) <= 0) {
		$("#advAmntRequested").next().html("<label>! This field is required.</label>");
		tmpError = true;		
	}
	if ($("#description").val() == "") {
		$("#description").next().html("<label>! This field is required.</label>");
		tmpError = true;		
	}
	return tmpError ? false : true;
}
// This method clears all error messages
function clearAllErrorMsgs() {
	$("#advAmntRequested").next().html("");
	$("#description").next().html("");
	$("#successMsgBudgetList").html("");
	$("#successMsgBudgetList").hide();
}
// On page load
$(document).ready(function() {

	    $("#cancelBtn").click(function() {
			clearAndCloseOverLay();
		});
	    
	    $('#advAmntRequested').autoNumeric({
			vMax : '999999999999.99'
		});


		$("#btnSubmit").click(function(event) {
			event.preventDefault();
			clearAllErrorMsgs();
			if (validateForm()) 
			{
				submitMyForm();
			}
			return false;

		});
	});