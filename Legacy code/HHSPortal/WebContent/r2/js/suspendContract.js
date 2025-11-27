
/**
 * This method submits Suspend Contract
 */
function submitSuspendContract(){
		pageGreyOut();
		var v_parameter = "&" + $("#suspendContractForm").serialize();
		var urlAppender = $("#suspendContractUrl").val();
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
					window.location.href = $("#duplicateRender").val();
				}
				 removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				 removePageGreyOut();
			}
		});
}

//On Load of the document
$(document)
		.ready(
				function() {
					hideUserPsswd();
					$('#chkSuspendContract').click(function() {
						if ($(this).attr("checked") == 'checked') {
							showUserPsswd();
						} else {
							hideUserPsswd();
						}
					});
					$("#suspendContractForm")
							.validate(
									{
										rules : {
											userName : {
												required : true
											},
											reason : {
												maxlength : 500,
												required : true
											},
											password : {
												//allowSpecialChar: ["A"," &*#!@%xA7"],
												//allowSpecialChar: ["A", " !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7"], //QC 9408 : spcl character updated
												required : true
											}
										},
										messages : {
											userName : {
												required : "! This field is required."
											},
											reason : {
												maxlength : "! Length should not exceed 500 characters.",
												required : "! This field is required."
											},
											password : {
												allowSpecialChar: "! Please enter valid text",
												required : "! This field is required."
											}
										},
										submitHandler : function(form) {
										if(validateTextArea('usrpsswdSuspendReason')){
											submitSuspendContract();
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
				});

/**
 * This method hides User password
 */
function hideUserPsswd(){
	$('#usrpsswdSuspend').hide();
	$('#usrpsswdSuspendReason').hide();
    $('#txtUsernameSus').hide();
    $('#txtPasswordSus').hide();
    $('#suspendContractButton').attr('disabled','disabled');
    $('span.error').empty();
    $("#ErrorDiv").hide();
}
/**
 * This method shows user password
 */
function showUserPsswd(){
	 $('#usrpsswdSuspend').show();
	 $('#usrpsswdSuspendReason').show();
     $('#txtUsernameSus').show();
     $('#txtPasswordSus').show();
     $('#suspendContractButton').attr('disabled',false);
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
