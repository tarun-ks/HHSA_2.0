/*On Load of the document
Updated Method in R4*/
$(document).ready(function() {
	var errorExist = $("#error").val();
	if (errorExist == "error") {
		$("#chktermsNConditions").attr('checked','checked');
		$("#chkAcknowledge").attr('checked','checked');
		$("#IranDivestmentAct").attr('checked','checked');
		$("#authenticate").show();
	} else {
		$("#authenticate").hide();
	}// validates submit proposal form
	$("#submitProposalForm").validate({
		rules : {
			userName : {
				required : true
			},
			password : {
				required : true
			}
		},
		messages : {
			userName : {
				required : "! This field is required."
			},
			password : {
				required : "! This field is required."
			}
		},
		submitHandler : function(form) {
			pageGreyOut();
			document.submitProposalForm.submit();
		},
		errorPlacement : function(error, element) {
			error.appendTo(element.parent().parent().find("span.error"));
		}
	});
	
	//on click of return to proposal Summary hyperlink
	$("#returnProposalSummaryPage").click(function(){
		pageGreyOut();
		$("#submitProposalForm").attr("action",
				$("#proposalSummaryUrl").val());
		document.submitProposalForm.submit();
	});
});

//Method to hide and unhide Authenticative Div
function hideUnhideAuthenticateDiv() {
	if ($("#chktermsNConditions").attr('checked')
			&& $("#chkAcknowledge").attr('checked')
			&& $("#IranDivestmentAct").attr('checked')){
		$("#authenticate").show();
	} else {
		$("#authenticate").hide();
		$("#userName").attr('value', '');
		$("#password").attr('value', '');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
	}
}