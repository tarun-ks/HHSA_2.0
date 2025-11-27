//This method executes on click of Renew Contract button
function submitUnSuspendContract(){
		pageGreyOut();
		var v_parameter = "&" + $("#unSuspendContractForm").serialize();
		var urlAppender = $("#unSuspendContractUrl").val();
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

// On document Load
$(document)
		.ready(
				function() {
					hideUserPsswd();
					$('#chkUnSuspendContract').click(function() {
						if ($(this).attr("checked") == 'checked') {
							showUserPsswd();
						} else {
							hideUserPsswd();
						}
					});
					$("#unSuspendContractForm")
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
										if(validateTextArea('usrpsswdUnsuspendReason')){
											submitUnSuspendContract();
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
 * This method hides the User Password
 */
function hideUserPsswd(){
	$('#usrpsswdUnsuspend').hide();
	$('#usrpsswdUnsuspendReason').hide();
    $('#txtUsernameUnsus').hide();
    $('#txtPasswordUnsus').hide();
    $('#unSuspendContractButton').attr('disabled','disabled');
    $('span.error').empty();
    $("#ErrorDiv").hide();
}

/**
 * This method shows the User Password
 */
function showUserPsswd(){
	 $('#usrpsswdUnsuspend').show();
	 $('#usrpsswdUnsuspendReason').show();
     $('#txtUsernameUnsus').show();
     $('#txtPasswordUnsus').show();
     $('#unSuspendContractButton').attr('disabled',false);
}

/**
 * This method sets the maximum length of Textarea.
 * updated in R5
 */
function setMaxLength(obj,maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}