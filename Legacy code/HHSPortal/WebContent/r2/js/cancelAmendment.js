//This js file is used for the page cancelAmendment.jsp
var errorFlag = true;
// On load of cancel amendment screen
$(document)
.ready(
		function() {
			$("#btnCancelAmendment").click(function(){
				 errorFlag = true;
				$("#ErrorDiv").hide();
				clearCommentError();
				
				if(trim(document.getElementById("commentArea").value)==""){
					$("#commentAreaErrorId").html(requiredKey);
					errorFlag = false;
				}
				// cancel amendment form validation
				$("#cancelAmendmentForm").validate({
								onfocusout: false,
							    focusCleanup: true,
								focusInvalid: false,
	                            rules: {
	                            	  
	                            	   userName:{required: true },
	                            	   password:{required: true }
	                                },
	                            messages: {
	                            	   
	                            	   userName :{ required: requiredKey },
	                            	   password:{ required: requiredKey }
	                                   
	                                   
	                                },
		
					//submit form once all validations are passed                                
		            submitHandler: function(form){
		            	if(errorFlag){
		            		if(validateTextArea('commentArea')){
			            	cancelAmendment();	
		            		}else{
		            			$("#ErrorDiv").html(invalidResponseMsg);
		            			$("#ErrorDiv").show();
		            			 return false;
		            		}
		            	}else {
		            		return false;
		            	}
		            	},errorPlacement: function(error, element) {
				         	error.appendTo(element.parent().parent().find("span.error"));
				  	  }
	            });
			  });
	    						
	});


/* This method executes on click of Renew Contract button
Updated Method in R4*/
function cancelAmendment(){
		pageGreyOut();
		var v_parameter = "&" + $("#cancelAmendmentForm").serialize();
		var urlAppender = $("#cancelAmendmentUrl").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(responseText) {
				if(responseText!=null && ""!=responseText){
					$("#ErrorDiv").html(responseText);
					$("#ErrorDiv").show();
				}else {
					clearAndCloseOverLay();
					window.location.href = $("#duplicateRenderAmendment").val();
				}
				 removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				 removePageGreyOut();
			}
		});
}	

//This method executes on click of checkbox and it will enable the submit button id checkbox is checked	
function enableSubmitButton(obj){
	if(obj.checked==true){
		$("#inputDiv").show();
		document.getElementById("btnCancelAmendment").disabled=false;
	}else{
		$("#inputDiv").hide();
		document.getElementById("btnCancelAmendment").disabled=true;
	}
	
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
 //Below function removes spaces from left and right of the string
function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

//Below function clear the commentArea error
function clearCommentError(){
	$("#commentAreaErrorId").html("");
}
	