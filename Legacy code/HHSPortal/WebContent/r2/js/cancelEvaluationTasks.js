//This js file is used for the page cancelEvaluationTask.jsp
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
        	$("#cancelEvaluationTasksForm").attr("action",
					$("#redirectURL").val());
			//submitting the page and redirecting to proposal evaluation status page
			document.cancelEvaluationTasksForm.submit();
        }
		//
	},
	error:function (xhr, ajaxOptions, thrownError)
	{
		showErrorMessagePopup();
		removePageGreyOut();
	}			
}

//On page load
$(document)
		.ready(
				function() {
	$("#authenticate").hide();//hide the username password fields
	$("#yesCancelEvaluationTasks").attr("disabled","disabled");	//disable the yes cancel evaluation task button	
	$("#authenticate").hide();
	//required validation for username password fields  
	$("#cancelEvaluationTasksForm").validate(
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
					$(document.cancelEvaluationTasksForm).ajaxSubmit(options);
				},
				errorPlacement : function(error,
						element) {
					error.appendTo(element.parent()
							.parent()
							.find("span.error"));
				}
			});
			
});

//method used to close the popup
function cancelOverLay() {
	$(".alert-box").hide();
	$(".overlay").hide();	
}

//method called on click of checkbox on cancelEvaluationTask jsp 
//to hide and unhide the username and password fields
function hideUnhideUsername(obj){
	if($(obj).attr('checked') || $(obj).attr('checked')=='checked'){
		$("#authenticate").show();
		$("#yesCancelEvaluationTasks").removeAttr("disabled");
		$("#yesCancelEvaluationTasks").addClass("redbtutton");
	}else{
		$("#authenticate").hide();
		$("#userName").attr('value','');
		$("#password").attr('value','');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
		$("#yesCancelEvaluationTasks").attr("disabled","disabled");
		$("#yesCancelEvaluationTasks").removeClass("redbtutton");
	}
}