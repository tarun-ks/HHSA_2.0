var options = 
{	
	success: function(responseText, statusText, xhr ) 
	{
	 	var $response=$(responseText);
        var data = $response.contents().find("#errorPlacement");
        if(data.size() > 0){
        	$("#errorPlacementWrapper").html(data);
        }else{
        	$("#overlay").closeOverlay();
        	window.location.href = $("#exitProcurementAnchor").attr("href")+"&success=cancelProcurement&resetSessionProcurement=true";
        }
		removePageGreyOut();
	},
	error:function (xhr, ajaxOptions, thrownError)
	{                     
		showErrorMessagePopup();
		removePageGreyOut();
	}			
}

// on page load 
$(document)
		.ready(
				function() {
	$("#authenticate").hide();
	$("#yesCancelProcurement").attr("disabled","disabled");		
	$("#cancelProcurementForm").validate(
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
					pageGreyOut();
					$(document.cancelProcurementForm).ajaxSubmit(options);
				},
				errorPlacement : function(error,
						element) {
					error.appendTo(element.parent()
							.parent()
							.find("span.error"));
				}
			});
			
});
// This method is used to hide the overlay		
function cancelOverLay() {
	$(".alert-box").hide();
	$(".overlay").hide();	
}

// This method hides/ unhides the authentication popup
function hideUnhideUsername(obj){
	if($(obj).attr('checked') || $(obj).attr('checked')=='checked'){
		$("#authenticate").show();
		$("#yesCancelProcurement").removeAttr("disabled");
		$("#yesCancelProcurement").addClass("redbtutton");
	}else{
		$("#authenticate").hide();
		$("#userName").attr('value','');
		$("#password").attr('value','');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
		$("#yesCancelProcurement").attr("disabled","disabled");
		$("#yesCancelProcurement").removeClass("redbtutton");
	}
}