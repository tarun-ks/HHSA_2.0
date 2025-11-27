	var lastData = null;
	$(function(){
		var $form = $("#firstNameId").closest('form');
		lastData = $form.serializeArray();
	});
			//Cancel button functionality while adding a staff
			function cancel(serviceAppId,businessAppId,section,subSection,elementId){		
				var $self=$(this);
				var $form = $("#firstNameId").closest('form');
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
								document.myformStaff.action = document.myformStaff.action+'&service_app_id='+serviceAppId+'&business_app_id='+businessAppId+'&next_action=cancelStaff&section='+section+"&subsection="+subSection+"&elementId="+elementId;
								document.myformStaff.submit();
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
					document.myformStaff.action = document.myformStaff.action+'&service_app_id='+serviceAppId+'&business_app_id='+businessAppId+'&next_action=cancelStaff&section='+section+"&subsection="+subSection+"&elementId="+elementId;
					document.myformStaff.submit();
				}
				
			
			}
			//Select from previously added Staff
			function setSelectionValue(obj,serviceAppId,businessAppId,section,subSection,elementId){
			var selectedStaff = $(obj).attr('value');
			$("#selectBoxValue").val(selectedStaff);
			document.myformStaff.action = document.myformStaff.action+'&service_app_id='+serviceAppId+'&business_app_id='+businessAppId+'&next_action=selectStaff&selectBoxValue='+selectedStaff+"&section="+section+"&subsection="+subSection+"&elementId="+elementId;  
			document.myformStaff.submit();  
			}
			 
			function resetTitle(){
				$("#ceoError").hide();
			}
			$(document).ready(function() {
			$("#staffPhone").fieldFormatter("XXX-XXX-XXXX");
			//perform the validation  like required field check, email check, maxlength check etc .on the staff screen.
			$("#myformStaff").validate({
						rules: {
							staffFirstName: {
								passSpecialChar: true,
								required: true,
								maxlength: 32,
								allowSpecialChar: ["A", ".,\\\'\\\" -"] },
							staffMidInitial: {
								maxlength: 1,
								allowSpecialChar: ["A", ".,\\\'\\\" -"] },
							staffLastName: {
								passSpecialChar: true,
								required: true,
								maxlength: 64,
								allowSpecialChar: ["A", ".,\\\'\\\" -"] },
							staffTitle: {noneSelected: true},
							staffPhone: {required: true,
								patternMatcher: "NNN-NNN-NNNN"},
							staffEmail: {required: true,
										maxlength: 128,
										email:true}
							
						},
						messages: {
							staffFirstName: {
								passSpecialChar: "! This field is required",
								required: "! This field is required.",
								maxlength:"Input should be less then 32 characters",
								allowSpecialChar: "! Only , . - and spaces are allowed as a special character."},
							staffMidInitial: {
								maxlength: "Middle name can have only 1 character",
								allowSpecialChar: "! Only , . - and spaces are allowed as a special character."} ,
							staffLastName:{
								passSpecialChar: "! This field is required",
								required: "! This field is required.",
								maxlength: "Input should be less then 64 characters",
								allowSpecialChar: "! Only , . - and spaces are allowed as a special character."},
							staffTitle: {required: "! This field is required."},
							staffPhone: {required: "! This field is required."},
							staffEmail:{required: "! This field is required.",
								maxlength: "Email should be less then 128 characters",
								email: "! Please enter a valid email address." }	
						},
						submitHandler: function(form){
							var selectStaffValue = $("#selectBox").val();
							document.myformStaff.action = document.myformStaff.action+'&service_app_id='+$("#hiddenServiceAppId").val()+'&business_app_id='+$("#hiddenBusinessAppId").val()+'&next_action=saveStaff&selectBoxValue='+selectStaffValue+"&section="+$("#hiddenSectionId").val()+"&subsection="+$("#hiddenSubSectionId").val()+"&elementId="+$("#hiddenElementId").val();
							document.myformStaff.submit();
						},
						 errorPlacement: function(error, element) {
						         error.appendTo(element.parent().parent().find("span.error"));
						  }
					});
			});
