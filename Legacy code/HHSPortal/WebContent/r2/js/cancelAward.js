/**
 * This file contains the method for award cancellation screen.
 * This js file is used for the page cancelAward.jsp
 **/
var options = 
{	
	success: function(responseText, statusText, xhr ) 
	{
	 	var $response=$(responseText);
        var data = $response.contents().find("#errorPlacement");
        if(data.size() > 0){
        	$("#errorPlacementWrapper").html(data);
        	removePageGreyOut();
		        } else {
			$("#overlay").closeOverlay();
			pageGreyOut();
			/* Start Updated in R5
			 Changes for Defect 7149 starts*/
			if ($('#finalizeAwardRenderUrl').length > 0) {
				var workflowId = $('#workflowId').val();
				var returnVal="&returnToAgencyTask=true";
				$("#finalizeAwardForm").attr(
						"action",
						$("#finalizeAwardRenderUrl").val() + returnVal
								+ '&taskUnlock=' + workflowId + '&paramValue=CancelAward');
				document.finalizeAwardForm.submit();
			/* Changes for Defect 7149 ends
			 End Updated in R5*/
			} else {
				contractId = $("#contractID").val();
				$("#cancelAwardForm").attr("action",
						$("#redirectURL").val() + "&contractId=" + contractId);
				document.cancelAwardForm.submit();
			}
		}
	},
	error:function (xhr, ajaxOptions, thrownError)
	{                     
		showErrorMessagePopup();
		removePageGreyOut();
	}			
}
/**
 * Code executed on page load. It performs certain functions on page load
and prepares set of values which are required for further processing
*/
$(document)
		.ready(
				function() {
	$("#authenticate").hide();/*hide the username password fields*/
	$("#yesCancelAward").attr("disabled","disabled");/*disable the yes cancel award button		
	required validation for username password fields*/
	$("#cancelAwardForm").validate(
			{
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
					/*page greyout on submit*/
					pageGreyOut();
					$(document.cancelAwardForm).ajaxSubmit(options);
				},
				errorPlacement : function(error,
						element) {
					error.appendTo(element.parent()
							.parent()
							.find("span.error"));
				}
			});
});
	
/**method used to close the popup*/
function cancelOverLay() {
	$(".alert-box").hide();
	$(".overlay").hide();	
}

/**method called on click of checkbox on cancelAward jsp 
to hide and unhide the username and password fields*/
function hideUnhideUsername(obj){
	if($(obj).attr('checked') || $(obj).attr('checked')=='checked'){
		$("#authenticate").show();
		$("#yesCancelAward").removeAttr("disabled");
		$("#yesCancelAward").addClass("redbtutton");
	}else{
		$("#authenticate").hide();
		$("#userName").attr('value','');
		$("#password").attr('value','');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
		$("#yesCancelAward").attr("disabled","disabled");
		$("#yesCancelAward").removeClass("redbtutton");
	}
}