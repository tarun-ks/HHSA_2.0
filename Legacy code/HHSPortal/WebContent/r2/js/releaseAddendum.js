//This method called when page is getting loaded and set the values
$(document).ready(function() {
	//performing jquery validations
	$("#releaseAddendumForm").validate({
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
			document.releaseAddendumForm.submit();
		},
		errorPlacement : function(error, element) {
			error.appendTo(element.parent().parent().find("span.error"));
		}
	});
});
