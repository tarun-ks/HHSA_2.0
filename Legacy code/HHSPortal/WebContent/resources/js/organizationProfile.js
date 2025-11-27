/**
 * This function is used when profile not found in the pop up window when user request comes 
 */
function profileNotFound(){
	$(".alert-box").hide();
	$(".overlay").hide();
	document.getElementById("memberInfo").style.display = 'block';
	document.getElementById("Approve").disabled=false;
	$("#staffPhone").val("").removeAttr("disabled");
	$("#staffTitle").val("").removeAttr("disabled");
	$("#memberAsUser").val("");
}
/**
 * This function is used when new user request link to the existing member
 * @returns true false
 */
function linkToExistingMember(){
	var boxList = $('input[id=selectedRadio]:radio:checked');
	if (boxList.length == 0) {
		$("#errorMessage").html("Please select at least one radio");
		return false;
	}else{
		$("input[id=selectedRadio]").each(function(i){
			if($(this).attr("checked") || $(this).attr("checked")=='checked'){
				$(".alert-box").hide();
				$(".overlay").hide();
				$("#staffPhone").removeAttr("disabled").val($("#existingStaffPhoneId").val());
				$("#staffTitle").removeAttr("disabled").val($("#existingStaffTitleId").val());		
				return true;
			}
		});
		document.getElementById("memberInfo").style.display = 'block';
		document.getElementById("Approve").disabled=false;
		$("#linkToMember").html("On Approval, this user will be linked to: <b>"+$("#memberName").val()+"</b>");
	}
}
/**
 * This function is used to set the value when user click on the radio button for linking to the existing member
 * @param radioValue id of the radio button
 * @param titleValue title of the member
 * @param phoneValue phone number of the member
 * @param memberName member name
 */
function radioSelectValue(radioValue,titleValue,phoneValue,memberName){
	$("#selectedValue").val(radioValue);
	$("#existingStaffTitleId").val(titleValue);
	$("#existingStaffPhoneId").val(phoneValue);
	$("#memberAsUser").val(radioValue);
	$("#memberName").val(memberName);
}

/**
 * This function check the email format
 * @param inputvalue input value
 * @returns true false
 */
function checkEmail(inputvalue){
	var pattern=/^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\.([a-zA-Z])+([a-zA-Z])+/;
	if(pattern.test(inputvalue)){
		return true;
	}else{
		return false;
	}
}

/**
 * This function is used to validate the form before submitting
 * @param addEditUser form type
 * @param section section value
 */

function submitForm(addEditUser,section){
	var loginUserEmailId = $("#userEmail").val();
	var emailIdToBeDeActivatted = $("#emailIdToBeDeactivated").val();
	var deActivatePopUp =false;
	if(null!= loginUserEmailId && null!=emailIdToBeDeActivatted && loginUserEmailId==emailIdToBeDeActivatted){
		deActivatePopUp = true;
	}
	var next_action;	
	var isValid = true;
	var isValidation = $("#memberNotUser").is(":visible");
	var isDeactivateAdmin='No';

	if(isValidation){
		$("input[type=text]").each(function(i){
			if($.trim($(this).val())==''){
				if($(this).attr("id")=='staffMidInitial' || $(this).attr("id")=='staffPhone' || $(this).attr("id")=='datepicker'){
				}else{
					$(this).next().remove("span");
					$(this).after("<span class='individualError' id='displayErrorMsg"+i+"'>! This field is required</span>");
					isValid = false;
				}
			}else{
				$(this).next().remove("span");
				if($(this).attr("id")=='staffPhone'){
					if($(this).val().length>0 && $(this).val().length<12){
						$(this).parent().attr("style","width:45%"); 
						$(this).after("<span class='individualError' id='displayErrorMsg"+i+"'>! Please enter valid number</span>");
						isValid = false;
					}
				}
				if($(this).attr("id")=='staffEmail'){
					if(!checkEmail($(this).val())){
						$(this).parent().attr("style","width:61%"); 
						$(this).after("<span style='width:54%' class='individualError' id='displayErrorMsg"+i+"'>! Invalid format. Email Address must contain an '@', and a '.'</span>");
						isValid = false;
					}
				}
			}
			if($.trim($(this).val())!=''){
				var restrictInputIds = $(this).attr("id");
				if(restrictInputIds=='staffMidInitial' || restrictInputIds=='staffLastName' || restrictInputIds=='staffFirstName'){
					lsResult =  "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7\n\r\t]+$";
					var re = new RegExp(lsResult);
			        if(!re.test($("#"+restrictInputIds).val())){
			        	$("#"+restrictInputIds).next().remove();
			        	$("#"+restrictInputIds).after("<span class='individualError' style='width:100%' id='displayErrorForNumberOther'>! Only (A-Z)(a-z), periods(.), dashes (-), apostrophes ('), quotes (\\\"), comma(,), and spaces are allowed.</span>");
						isValid = false;
			        }
				}
			}
		});
	}
	if(addEditUser=='addUser'){
		next_action = "saveOrgMember";
	}else{
		if($("#staffPhone").val().length>0 && $("#staffPhone").val().length<12){
			$("#staffPhone").parent().attr("style","width:45%"); 
			$("#staffPhone").next().remove("span");
			$("#staffPhone").after("<span class='individualError' id='displayErrorMsgPhone'>! Please enter valid number</span>");
			isValid = false;
		}
		var checkValue = $("input:radio[name='systemRole']");
		if(checkValue.size() > 0){
			if($("input:radio[name='systemRole']").is(":checked")) {    
				var obj = $("input:radio[name='systemRole']")[0];
				$(obj).parent().next().next().html("");
			}else{
				var obj = $("input:radio[name='systemRole']")[0];
				$(obj).next().remove("span");
				$(obj).parent().next().next().html("<span class='individualError' style='float: none;' id='displayErrorMsg'>! This field is required</span>");
				isValid = false;
			}

			if($("input:radio[name='accountAdmin']").is(":checked")) {  
				var obj = $("input:radio[name='accountAdmin']")[0];
				$(obj).next().remove("span");
			}else{
				var obj = $("input:radio[name='accountAdmin']")[0];
				$(obj).next().remove("span");
				$(obj).after("<span class='individualError' id='displayErrorMsg'>! This field is required</span>");
				isValid = false;
			}
		}
		
		
		if($("input:checkbox[name='deActivatedUser']").is(":checked")) {
			if($("#lsAdminCount").val() >=1 && $("#adminUserId").val()=='Yes' && deActivatePopUp){
				isDeactivateAdmin = 'Yes';
			}	
		}else{
			var obj = $("input:checkbox[name='deActivatedUser']")[0];
			$(obj).next().remove("span");
			$("#displayErrorMsgAdminUser").remove();
		}
		
		
		if($("input:radio[name='accountAdmin']").is(":checked")) {
			if($("#lsAdminCount").val()==1 && $("#adminUserIdNo").val()=='Yes'){
				$("input[name=accountAdmin]").each(function(i){
					$(this).next().remove("span");
					var deActivate = $("input:checkbox[name='deActivatedUser']").is(":checked");
					if($(this).attr("checked") || $(this).attr("checked")=='checked'){
						$("#displayErrorMsgRemove").remove();
						if($("#adminUserId").val()=='No'){
							$(this).next().remove("span");
							$(this).after("<span class='individualError' id='displayErrorMsgAdminUser'>! There must be at least one user in an administrator role. You are attempting to deactivate the only administrator</span>");
							isValid = false;
						}
					}
				});
			}else{
				$("#displayErrorMsgAdminUser").remove();
			}
		}
		
		if($("input:checkbox[name='removeMember']").is(":checked")) {
			if($("#lsAdminCount").val()==1 && $("#adminUserIdNo").val()=='Yes' && $("#datepicker").val()!=''){
				$("#displayErrorMsgAdminUser").remove();
				var obj = $("input:checkbox[name='removeMember']")[0];
				$(obj).next().remove("span");
				$(obj).after("<span class='individualError' id='displayErrorMsgRemove'>! There must be at least one user in an administrator role. You are attempting to deactivate the only administrator</span>");
				isValid = false;
			}
		}else{
			var obj = $("input:checkbox[name='removeMember']")[0];
			$(obj).next().remove("span");
			$("#displayErrorMsgRemove").remove();
		}
		
		next_action = "saveEditMembers";
	}

	if($("#removeMember").attr("checked")){
		if($("#datepicker").val()==''){
			$("#datepicker").next().next().remove();
			$("#datepicker").next().after("<span class='individualError'>! This field is required</span>");
			isValid = false;
		}else{
			$("#datepicker").next().next().remove();
			$("#datepicker").parent().attr("style","");
			$("#datepicker").parent().attr("style","30%");
			$("input[type='text']").each(function(){
				if($(this).attr("id")=='datepicker'){
					var verify = verifyDate(this);
					if(!verify){
						isValid = false;
					}
				}
			});
			
		}
	}
	if($("#staffTitle").val()==-1){
		$("#staffTitle").next().remove("span");
		$("#staffTitle").after("<span class='individualError' id='displayErrorMsgTitle'>! This field is required</span>");
		isValid = false;
	}else{
		$("#staffTitle").next().remove("span");
	}
	if(isValid){
		if(isDeactivateAdmin == 'Yes'){
				$('<div id="dialogBox" class="tabularCustomHead"></div>').appendTo('body')
				.html('<div class="pad6 clear promptActionMsg">Are you sure that you want to deactivate your account ?</div>')
				.dialog({
					modal: true, title: 'Account Deactivation Confirmation', zIndex: 10000, autoOpen: true,
					width: 'auto', modal: true, resizable: false, draggable:false,
					dialogClass: 'dialogButtons',
					buttons: {
						No: function () {
							$(this).dialog("close");
						},
						Yes: function () {
							if($("#lsAdminCount").val()==1 && $("#adminUserId").val()=='Yes'){
								$("#displayErrorMsgRemove").remove();
								var obj = $("input:checkbox[name='deActivatedUser']")[0];
								$(obj).next().remove("span");
								$(obj).after("<span class='individualError' id='displayErrorMsgDeactivate'>! There must be at least one user in an administrator role. You are attempting to deactivate the only administrator</span>");
								isValid = false;
							}else{
								document.orgForm.action = document.orgForm.action+'&next_action='+next_action+'&action=manageMembers&section='+section+"&subsection=memberandusers";
								document.orgForm.submit();
							}
							$(this).dialog("close");
						}
					},
					close: function (event, ui) {
						$(this).remove();
					}
				}).css("font-size", "12px");
				$("div.dialogButtons div button:nth-child(1)").find("span").addClass("graybtutton");
	    }
		if(isDeactivateAdmin == 'No'){
			document.orgForm.action = document.orgForm.action+'&next_action='+next_action+'&action=manageMembers&section='+section+"&subsection=memberandusers";
			document.orgForm.submit();
		}
	 }
	}

/**
 * This function is used to remove the error message
 * @param obj input request object
 */
function removeError(obj){
	if($(obj).val()!=''){
		$("#datepicker").parent().attr("style","30%");
		$(obj).next().next().remove();
	}
}

/**
 * This function is used to set the phone number format after loading the page
 */
var lastData = null;
$(function(){
	$('#staffFirstName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
	$('#staffMidInitial').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
	$('#staffLastName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
	
	var $form = $("#editStaffId").closest('form');
	lastData = $form.serializeArray();
	$("input[name='staffPhone']").fieldFormatter("XXX-XXX-XXXX");
	if($("#deactivateUser").val()=='on'){
		$("#deActivatedUser").attr("checked","checked");
	}
	$("span").each(function(i){
		if($(this).attr("class")=='formfield'){
		}
	});
});

/**
 * This function is used to show the dialog for the unsave data  
 */
function cancelButton(){
	var $self=$(this);
	var $form = $("#editStaffId").closest('form');
	var isSame = false;
	data = $form.serializeArray();
	if(lastData != null){
		if($(lastData).compare($(data))){
			isSame = true;
		}
	}
	if(!isSame && lastData != null){
		$('<div id="dialogBox"></div>').appendTo('body')
		.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
		.dialog({
			modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
			width: 'auto', modal: true, resizable: false, draggable:false,
			dialogClass: 'dialogButtons',
			buttons: {
				OK: function () {
					document.orgForm.action = document.orgForm.action+"&next_action=cancleButton&subsection=memberandusers&action=manageMembers";
					document.orgForm.submit();
					$(this).dialog("close");
				},
				Cancel: function () {
					$(this).dialog("close");
				}
			},
			close: function (event, ui) {
				$(this).remove();
			}
		});
		$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
	}else{
		document.orgForm.action = document.orgForm.action+"&next_action=cancleButton&subsection=memberandusers&action=manageMembers";
		document.orgForm.submit();
	}
}

/**
 * This function is used to show and hide the calender input box 
 * @param obj input request object
 */
function showHideCalander(obj){
	if(obj.checked){
		$("#calanderId").show();
		$("#emptyCheckBox").show();
		$("#displayErrorMsgRemove").remove();
	}else{
		$("#calanderId").hide();
		$("#emptyCheckBox").hide();
		$("#datepicker").val("");
		$("#datepicker").next().next().remove("span");
		$("#datepicker").parent().next().html("");
		$("#displayErrorMsgRemove").remove();
	}
}
/**
 * This function is used to open the pop up window to link a user with existing member
 * @param obj input request object
 * @returns true false 
 */
function displayExistingMember(obj){
	$("#next_action").val("displayOrgMember");
	$("#showExistingMember").val("true");
	var options={	
			success: function(responseText, statusText, xhr){
				var $response=$(responseText);
				var data = $response.contents().find(".overlaycontent1");
				$("#displayshared").show();
				$("#displayshared").empty();
				$("#displayshared").html(data);
				$(".overlay").launchOverlay($(".alert-box"), $(".exit-panel"), "650px");
			},
			error:function (xhr, ajaxOptions, thrownError){                     
				showErrorMessagePopup();
				removePageGreyOut();
			}
	};
	$(document.editRequestForm).ajaxSubmit(options);
	return false;		
}

/**
 * This function is used to validate the form when user tries to submit the edit request form (when user request comes for approve)
 * @param value approve or deny
 */
function userRequest(value){
	var isValid = true;
	if(value=='denyUserRequest'){
		$("#next_action").val("denyUserRequest");
	}else{
		if($("input:radio[name='systemRole']").is(":checked")) {	
			var obj = $("input:radio[name='systemRole']")[0];
			$(obj).parent().next().next().html("");
		}else{
			var obj = $("input:radio[name='systemRole']")[0];
			$(obj).parent().next().next().html("<span class='individualError' style='float: none;' id='displayErrorMsg'>! This field is required</span>");
			isValid = false;
		}

		if($("input:radio[name='accountAdmin']").is(":checked")) {	
			var obj = $("input:radio[name='accountAdmin']")[0];
			$(obj).next().remove("span");
		}else{
			var obj = $("input:radio[name='accountAdmin']")[0];
			$(obj).next().remove("span");
			$(obj).after("<span class='individualError' id='displayErrorMsg'>! This field is required</span>");
			isValid = false;
		}
		if($("#staffTitle").val()==-1){
			$("#staffTitle").next().remove("span");
			$("#staffTitle").after("<span class='individualError' id='displayErrorMsg'>! This field is required</span>");
			isValid = false;
		}else{
			$("#staffTitle").next().remove("span");
		}
		
		if($("#staffPhone").val().length>0 && $("#staffPhone").val().length<12){
			$("#staffPhone").parent().attr("style","width:45%"); 
			$("#staffPhone").next().remove("span");
			$("#staffPhone").after("<span class='individualError' id='displayErrorMsgPhone'>! Please enter valid number</span>");
			isValid = false;
		}else{
			$("#staffPhone").next().remove("span");
		}
		if(isValid){
			$("#next_action").val("approveUserRequest");
			document.editRequestForm.action = document.editRequestForm.action+'&next_action=approveUserRequest&action=manageMembers&subsection=memberandusers';
			document.editRequestForm.submit();
		}
	}
}
/**
 * This function is used to set the admin user value
 * @param yes no
 */
function setAdminUser(value){
	$("#adminUserId").val(value);
}
/**
 * This function is used to set the permission level 
 * @param value 1 or 2
 */
function setPermissionLevelValue(value){
	$("#permissionLevelId").val(value);
}

/**
* This function is used to set the permission type and level 
* @param value 1 or 2
*/
function setPermissionLevelAndTypeValue(value1,value2){
	$("#permissionLevelId").val(value1);
	$("#permissionTypeId").val(value2);
	
	// Start Added in R5
	if(value1=="Read-Only"){
		$("input[name='accountAdmin']")[1].click();
		$("input[name='accountAdmin']").attr("disabled", true);
	}else{
		$("input[name='accountAdmin']").attr("disabled", false);
	}
	// End Added in R5
}

