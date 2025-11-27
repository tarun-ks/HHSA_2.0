//On Page load
$(document)
		.ready(
				function() {
	$("#authenticate").hide();
	$("#yesCancelModification").attr("disabled","disabled");		
	$("#cancelBudgetModificationForm").validate(
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
					submitcancelBudgetModification();
				},
				errorPlacement : function(error,
						element) {
					error.appendTo(element.parent()
							.parent()
							.find("span.error"));
				}
			});
			
});
// This method closes the overlay
function closeOverLay() {
	$(".alert-box").hide();
	$(".overlay").hide();	
}

// this method hides/unhides the login popup
function hideUnhideUsername(obj){
	if($(obj).attr('checked') || $(obj).attr('checked')=='checked'){
		$("#authenticate").show();
		$("#yesCancelModification").removeAttr("disabled");
		$("#yesCancelModification").addClass("redbtutton");
	}else{
		$("#authenticate").hide();
		$("#userName").attr('value','');
		$("#password").attr('value','');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
		$("#yesCancelModification").attr("disabled","disabled");
		$("#yesCancelModification").removeClass("redbtutton");
	}
}


//This method executes on click of Cancel Budget Modification button
function submitcancelBudgetModification(){
		pageGreyOut();
	    var v_parameter = "&" + $("#cancelBudgetModificationForm").serialize();
		var urlAppender = $("#cancelBudgetModificationUrl").val();
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