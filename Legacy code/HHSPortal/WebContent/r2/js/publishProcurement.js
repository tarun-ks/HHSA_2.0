//This method called when page is getting loaded and set the values
$(document).ready(function() {
	$(".ValidationError").css("color", "red");
					//performing jquery validations
					$("#publishProcurementform").validate(
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
											//submitting the page
											document.publishProcurementform.submit();
										},
										errorPlacement : function(error, element) {
											error.appendTo(element.parent().parent().find("span.error"));
										}
									});
							
				});
							