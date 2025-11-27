var options = 
{	
	success: function(responseText, statusText, xhr ) 
	{
	 	var $response=$(responseText);
        var data = $response.contents().find("#errorPlacement");
        var data1 = $response.contents().find("#closeProcurmentSuccess");
        if(data.size() > 0){
        	$("#errorPlacementWrapper").html(data);
        }else{
        	$("#overlay").closeOverlay();
        	if(data1.size() > 0){
        		window.location.href = $("#redirectURL").val()+"&closeProcurmentSuccess=NO";
        	}else{
        		window.location.href = $("#exitProcurementAnchor").attr("href")+"&success=closeProcurement&resetSessionProcurement=true";
        	}
        }
		removePageGreyOut();
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
	$("#authenticate").hide();
	$("#yesCloseProcurement").attr("disabled","disabled");		
	$("#closeProcurementForm").validate(
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
					$(document.closeProcurementform).ajaxSubmit(options);
				},
				errorPlacement : function(error,
						element) {
					error.appendTo(element.parent()
							.parent()
							.find("span.error"));
				}
			});
			
});
// this method closes the overlay		
function closeOverLay() {
	$(".alert-box").hide();
	$(".overlay").hide();	
}

// This method  hide/unhide the authenticate popup 
function hideUnhideUsername(obj){
	if($(obj).attr('checked') || $(obj).attr('checked')=='checked'){
		$("#authenticate").show();
		$("#yesCloseProcurement").removeAttr("disabled");
		$("#yesCloseProcurement").addClass("redbtutton");
	}else{
		$("#authenticate").hide();
		$("#userName").attr('value','');
		$("#password").attr('value','');
		$("#userName").parent().next().html('');
		$("#password").parent().next().html('');
		$("#yesCloseProcurement").attr("disabled","disabled");
		$("#yesCloseProcurement").removeClass("redbtutton");
	}
}