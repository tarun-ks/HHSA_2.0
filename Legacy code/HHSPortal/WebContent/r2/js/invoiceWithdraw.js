//This method closes the overlay.
function clearAndCloseOverLay() {
	$("#overlayContent").html("");
	$(".overlay").closeOverlay();
}
//This method executes on click of checkbox and it will enable the submit button id checkbox is checked	
function enableSubmitButton(obj){
	if(obj.checked==true){
		$("#displayDiv").show();
		document.getElementById("withdrawButton").disabled=false;
	}else{
		$('span.error').empty();
		$("#ErrorDiv").empty();
		$("#ErrorDiv").hide();
		$("#displayDiv").hide();
		document.getElementById("withdrawButton").disabled=true;
	}
	
}
//Function to authenticate user credentials from withdraw invoice screen.
function submitWithdrawInvoice(){
	pageGreyOut();
	var v_parameter = "&" + $("#invoiceWithdrawSubmitForm").serialize();
	var urlAppender = $("#hiddenWithdrawInvoiceUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(responseText) {
			if(responseText!=null && ""!=responseText){
				$("#ErrorDiv").html(responseText);
				$("#ErrorDiv").show();
				removePageGreyOut();
			}else {
				clearAndCloseOverLay();
				document.invoiceFilterForm.submit();
				//window.location.href = $("#duplicateRenderInvoice").val();
			}
			 //removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			 removePageGreyOut();
		}
	});
}
// this method validates the invoice withdraw form
function onLoad(){
	$("#invoiceWithdrawSubmitForm")
	.validate(
			{
				rules : {
					userName : {
						required : true
					},
					reason : {
						maxlength : 300,
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
					reason : {
						maxlength : "! Length should not exceed 300 characters.",
						required : "! This field is required."
					},
					password : {
						required : "! This field is required."
					}
				},
				submitHandler : function(form) {
					if(validateTextArea('textEnter')){
					submitWithdrawInvoice();
					}else{
            			$("#ErrorDiv").html(invalidResponseMsg);
            			$("#ErrorDiv").show();
            			 return false;
            		}
				},
				errorPlacement : function(error,
						element) {
					error.appendTo(element.parent()
							.parent()
							.find("span.error"));
					
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