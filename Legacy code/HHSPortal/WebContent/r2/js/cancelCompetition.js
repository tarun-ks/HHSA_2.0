//This js file is used for the page closeGroup.jsp
var options = 
{	
	success: function(responseText, statusText, xhr ) 
	{
	 	var $response=$(responseText);
	 	if($response.contents().find("#cancelStatusFlag").size()>0)
	 	{
	 		$("#overlay").closeOverlay();
	        pageGreyOut();
	       	window.location.href = $("#redirectURL").val()+"&cancelCompetition=true&cancelStatusFlag=false";
	 	}
	 	else if($response.contents().find("#errorPlacement").size() >0)
	 	{
	 		var data = $response.contents().find("#errorPlacement");
	        $("#errorPlacementWrapper").html(data);
	        removePageGreyOut();
	 	}
	 	else{
	 		$("#overlay").closeOverlay();
        	pageGreyOut();
        	window.location.href = $("#redirectURL").val()+"&cancelCompetition=true";
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
	$("#yesCancelCompetition").attr("disabled","disabled");//disable the yes close evaluation group button	
	$("#authenticate").hide();
	//required validation for user name and password fields  
	$("#cancelCompetitionForm").validate(
			{
				rules : {
					userName : {
						required : true
					},
					password : {
						required : true
					},
					comments : {
						required : true
					}
				},
				messages : {
					userName : {
						required : "! This field is required."
					},
					password : {
						required : "! This field is required."
					},
					comments : {
						required : "! This field is required."
					}
				},
				submitHandler : function(form) {
					//page greyout on submit
					pageGreyOut();
					$(document.cancelCompetitionForm).ajaxSubmit(options);
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

//method called on click of check box on closeGroup jsp 
//to hide and unhide the user name and password fields
function hideUnhideUsername(obj){
	if($(obj).attr('checked') || $(obj).attr('checked')=='checked'){
		$("#authenticate").show();
		$("#yesCancelCompetition").removeAttr("disabled");
		$("#yesCancelCompetition").addClass("redbtutton");
	}else{
		$("#authenticate").hide();
		$("#userName").attr('value','');
		$("#password").attr('value','');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
		$("#yesCancelCompetition").attr("disabled","disabled");
		$("#yesCancelCompetition").removeClass("redbtutton");
	}
}