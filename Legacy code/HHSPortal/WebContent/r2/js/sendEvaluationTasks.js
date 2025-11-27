//This js file is used for the page sendEvaluationTask.jsp
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
        	window.location.href = $("#redirectURL").val();
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
	$("#sendEvaluationTask").attr("disabled","disabled");//	disable the send task button		
	$("#chksendEvaluation2").attr("disabled","disabled");// disable the Yes, I understand that Non-Responsive Proposals will not be evaluated checkbox
	//required validation for username password fields  
	$("#sendEvaluationForm").validate(
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
					$(document.sendEvaluationform).ajaxSubmit(options);
				},
				errorPlacement : function(error,
						element) {
					error.appendTo(element.parent()
							.parent()
							.find("span.error"));
				}
			});
			
});
	
//method used to close the pop up
function closeOverLay() {
	$(".alert-box").hide();
	$(".overlay").hide();	
}

//this function is called on click of first checkbox and 
//enables the second checkbox if the first one is enabled else disable it. 
function checkboxEnable(){
	if($("#chksendEvaluation1").attr('checked') || $("#chksendEvaluation1")=='checked'){
		$("#chksendEvaluation2").removeAttr("disabled");
	}
	else{
		$("#chksendEvaluation2").attr("disabled","disabled");
		$("#chksendEvaluation2").removeAttr("checked");
		$("#authenticate").hide();
		$("#userName").attr('value','');
		$("#password").attr('value','');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
		$("#sendEvaluationTask").attr("disabled","disabled");
		$("#sendEvaluationTask").removeClass("greenbtutton");
	}
}

//method called on click of checkbox on sendEvaluationTask jsp 
//to hide and unhide the username and password fields
function hideUnhideUsername(obj){
	if($(obj).attr('checked') || $(obj).attr('checked')=='checked'){
		$("#authenticate").show();
		$("#sendEvaluationTask").removeAttr("disabled");
		$("#sendEvaluationTask").addClass("greenbtutton");
	}else{
		$("#authenticate").hide();
		$("#userName").attr('value','');
		$("#password").attr('value','');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
		$("#sendEvaluationTask").attr("disabled","disabled");
		$("#sendEvaluationTask").removeClass("greenbtutton");
	}
}