//This js file is used for the page closeAllSubmission.jsp
var options = 
{	
	success: function(responseText, statusText, xhr ) 
	{
	 	var $response=$(responseText);
        var data = $response.contents().find("#errorPlacementAllSubmission");
        if(data.size() > 0){
        	$("#errorPlacementWrapperAllSubmission").html(data);
        	removePageGreyOut();
        }else{
        	$("#overlay").closeOverlay();
        	pageGreyOut();
        	window.location.href = $("#redirectURL").val()+"&closeSubmittion=closeAllSubmittionSuccess";
        }
	},
	error:function (xhr, ajaxOptions, thrownError)
	{                     
		showErrorMessagePopup();
		removePageGreyOut();
	}			
}

//Code executed on page load. It performs certain functions on page load
//and prepares set of values which are required for further processing
$(document)
		.ready(
				function() {
	$("#authenticateAllSubmission").hide();//hide the username password fields
	$("#yesCloseAllSubmission").attr("disabled","disabled");//disable the yes close all submissions button	
	//required validation for user name and password fields  
	$("#closeAllSubmissionForm").validate(
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
					//page greyout on submit
					pageGreyOut();
					$(document.closeAllSubmissionForm).ajaxSubmit(options);
				},
				errorPlacement : function(error,
						element) {
					error.appendTo(element.parent()
							.parent()
							.find("span.error"));
				}
			});
			
});

// method used to close the popup
function closeOverLay() {
	$(".alert-box").hide();
	$(".overlay").hide();	
}

//method called on click of check box on closeAllSubmission jsp 
//to hide and unhide the user name and password fields
function hideUnhideUsername(obj){
	if($(obj).attr('checked') || $(obj).attr('checked')=='checked'){
		$("#authenticateAllSubmission").show();
		$("#yesCloseAllSubmission").removeAttr("disabled");
		$("#yesCloseAllSubmission").addClass("redbtutton");
	}else{
		$("#authenticateAllSubmission").hide();
		$("#userName").attr('value','');
		$("#password").attr('value','');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
		$("#yesCloseAllSubmission").attr("disabled","disabled");
		$("#yesCloseAllSubmission").removeClass("redbtutton");
	}
}