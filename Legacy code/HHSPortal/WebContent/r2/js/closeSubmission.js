//This js file is used for the page closeSubmission.jsp
var options = 
{	
	success: function(responseText, statusText, xhr ) 
	{
	 	var $response=$(responseText);
        var data = $response.contents().find("#errorPlacement");
        if(data.size() > 0){
        	$("#errorPlacementWrapper").html(data);
        	removePageGreyOut();
        }else{
        	$("#overlay").closeOverlay();
        	pageGreyOut();
        	window.location.href = $("#redirectURL").val()+"&closeSubmittion=closeSubmittionSuccess";
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
	$("#authenticate").hide();//hide the username password fields
	$("#yesCloseSubmissions").attr("disabled","disabled");//	disable the yes close submission button	
	$("#authenticate").hide();
	//required validation for username password fields  
	$("#closeSubmissionsForm").validate(
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
					$(document.closeSubmissionsForm).ajaxSubmit(options);
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

//method called on click of checkbox on closeSubmission jsp 
//to hide and unhide the username and password fields
function hideUnhideUsername(obj){
	if($(obj).attr('checked') || $(obj).attr('checked')=='checked'){
		$("#authenticate").show();
		$("#yesCloseSubmissions").removeAttr("disabled");
		$("#yesCloseSubmissions").addClass("redbtutton");
	}else{
		$("#authenticate").hide();
		$("#userName").attr('value','');
		$("#password").attr('value','');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
		$("#yesCloseSubmissions").attr("disabled","disabled");
		$("#yesCloseSubmissions").removeClass("redbtutton");
	}
}