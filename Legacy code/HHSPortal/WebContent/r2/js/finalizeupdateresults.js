//Overlay is closed when cancel button is clicked on 
var options = {
	success : function(responseText, statusText, xhr) {
		var $response = $(responseText);
		var data = $response.contents().find("#errorPlacement");
		if (data.size() > 0) {
			$("#errorPlacementWrapper").html(data);
			removePageGreyOut();
		} else {
			$("#overlay").closeOverlay();
			pageGreyOut();
			window.location.href = $("#redirectURL").val();
		}
	},
	error : function(xhr, ajaxOptions, thrownError) {
		showErrorMessagePopup();
		removePageGreyOut();
	}
}
/**
 * This method called when page is getting loaded; authentication form is hidden initially 
 */
$(document).ready(function() {
	$("#procurementValue").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
	$("#awardAmt").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
	
	$("#authenticate").hide();
	$("#yesCancelAward").attr("disabled", "disabled");
	$("#finalizeUpdateAwardForm").validate({
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
			$(document.finalizeUpdateAwardForm).ajaxSubmit(options);
		},
		errorPlacement : function(error, element) {
			error.appendTo(element.parent().parent().find("span.error"));
		}
	});

});

//Overlay is closed when cancel button is clicked on 

function cancelOverLay() {
	$(".alert-box").hide();
	$(".overlay").hide();
}
//when checkbox is clicked on; authentication fields display and vice-versa
function hideUnhideUsername(obj) {
	if ($(obj).attr('checked') || $(obj).attr('checked') == 'checked') {
		$("#authenticate").show();
		$("#yesCancelAward").removeAttr("disabled");
		$("#yesCancelAward").addClass("button");
	} else {
		$("#authenticate").hide();
		$("#userName").attr('value', '');
		$("#password").attr('value', '');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
		$("#yesCancelAward").attr("disabled", "disabled");
		$("#yesCancelAward").removeClass("button");
	}
}
