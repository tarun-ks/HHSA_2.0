// On page load
$(document)
		.ready(
				function() {
	$("#authenticate").hide();
	$("#btnYesDeleteBudgetUpdate").attr("disabled","disabled");		
	$("#confirmDeleteBudgetUpdateForm")
	.validate(
		
			{
				
				rules : {
					deleteBudgetUpdateComment : {
						maxlength : 500,
						required : true
					},
					userName : {
						required : true
					},
					password : {
						required : true
					}
				},
				messages : {
					deleteBudgetUpdateComment : {
						maxlength : "! Length should not exceed 500 characters.",
						required : "! This field is required."
					},					
					userName : {
						required : "! This field is required."
					},
					password : {
						required : "! This field is required."
					}
				},
				submitHandler : function(form) {
					if(validateTextArea('deleteBudgetUpdateComment')){
						submitDeleteBudgetUpdate();
					}else{
            			$("#ErrorDiv").html(invalidResponseMsg);
            			$("#ErrorDiv").show();
            			 return false;
            		}
				},
				errorPlacement : function(error,
						element) {
					error.appendTo(element.parent().parent()
					.find("span.error"));
				}
			});		
});
// This method closes the alertbox		
function closeOverLay() {
	$(".alert-box").hide();
	$(".overlay").hide();	
}

//This method  hides/unhides the authentication popup
function hideUnhideUsername(obj){
	if($(obj).attr('checked') || $(obj).attr('checked')=='checked'){
		$("#authenticate").show();
		$("#btnYesDeleteBudgetUpdate").removeAttr("disabled");
		$("#btnYesDeleteBudgetUpdate").addClass("redbtutton");
	}else{
		$("#authenticate").hide();
		$("#txtDeleteBudgetUpdateUserName").attr('value','');
		$("#txtDeleteBudgetUpdatePassword").attr('value','');
		$("#txtDeleteBudgetUpdateUserName").parent().next().html('');
		$("#txtDeleteBudgetUpdatePassword").parent().next().html('');
		$("#btnYesDeleteBudgetUpdate").attr("disabled","disabled");
		$("#btnYesDeleteBudgetUpdate").removeClass("redbtutton");
	}
}


//This method executes on click of Cancel button
function submitDeleteBudgetUpdate(){
		pageGreyOut();
		$("#delBudgetUpdateComment").val($("#deleteBudgetUpdateComment").val());
		var v_parameter = "&" + $("#confirmDeleteBudgetUpdateForm").serialize();		
		var urlAppender = $("#hdnConfirmDeleteBudgetUpdate").val();
		jQuery.ajax({
			type : "POST",	
			url : urlAppender,
			data : v_parameter,
			success : function(result) {
					if(result["error"]==0){
						clearAndCloseOverLay();
						window.location.href = $("#duplicateRender").val();
					}else{
					$("#ErrorDiv").html(result["message"]);
					$("#ErrorDiv").show();
				}
				 removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				 removePageGreyOut();
			}
		});
}



/**
 * Function sets the maximum length of Textarea.
 * updated in R5
 */
function setMaxLength(obj,maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}