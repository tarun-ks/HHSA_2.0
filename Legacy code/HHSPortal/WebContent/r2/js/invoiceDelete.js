//This method closes the overlay.
function clearAndCloseOverLay() {
	$("#overlayContent").html("");
	$(".overlay").closeOverlay();
}
//This method executes on click of checkbox and it will enable the submit button id checkbox is checked	
function enableSubmitButton(obj){
	if(obj.checked==true){
		$("#displayDiv").show();
		document.getElementById("deleteInvoiceButton").disabled=false;
		$('span.error').empty();
	}else{
		$("#displayDiv").hide();
		document.getElementById("deleteInvoiceButton").disabled=true;
	}
	
}

//This method executes on click of delete invoice
function submitDeleteInvoice(){
		pageGreyOut();
		var v_parameter = "&" + $("#invoiceDeleteForm").serialize();
		var urlAppender = $("#hiddenDeleteInvoiceUrl").val();
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
					//refresh();
					document.invoiceFilterForm.submit();
				}
				
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				 removePageGreyOut();
			}
		});
}
// This method validates the invoice delete form
function onLoad(){
	$("#invoiceDeleteForm")
	.validate(
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
					submitDeleteInvoice();
				},
				errorPlacement : function(error,
						element) {
					error.appendTo(element.parent()
							.parent()
							.find("span.error"));
				}
			});
}
