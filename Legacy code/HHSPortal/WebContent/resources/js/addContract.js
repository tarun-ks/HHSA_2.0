var emailFlag = true;
var passwordFlag = true;
var minimumFlag = true;
var lastData = null;
//This method show/hide the div on the page for nyc/non-nyc type on page load.
$(function(){
	var hiddenFlag = $("#contractHidden").val();
	var $form = $("#contractTypeDivId").closest('form');
	
$("#phoneId").fieldFormatter("XXX-XXX-XXXX");
	
$('#contractBudget').autoNumeric({vMax: '999999999999.99'});
if( $("#contractType").val()=='NYC Government'){
		if(!hiddenFlag) {
			$("#nycAgency").show().find("select, input, textarea").attr('disabled',false);
		}
		$("#funderName").hide().find("select, input, textarea").attr('disabled',true);
		$("#firstName").hide().find("select, input, textarea").attr('disabled',true);
		$("#midName").hide().find("select, input, textarea").attr('disabled',true);
		$("#lastName").hide().find("select, input, textarea").attr('disabled',true);
		$("#title").hide().find("select, input, textarea").attr('disabled',true);
		$("#phone").hide().find("select, input, textarea").attr('disabled',true);
		$("#email").hide().find("select, input, textarea").attr('disabled',true);
		$("#reference").hide().find("select, input, textarea").attr('disabled',true);
	}else{
		$("#nycAgency").hide().find("select, input, textarea").attr('disabled',true);
		$("#funderName").show().find("select, input, textarea").attr('disabled',false);
		$("#firstName").show().find("select, input, textarea").attr('disabled',false);
		$("#midName").show().find("select, input, textarea").attr('disabled',false);
		$("#lastName").show().find("select, input, textarea").attr('disabled',false);
		if(!hiddenFlag) {
			$("#title").show().find("select, input, textarea").attr('disabled',false);
		}
		$("#phone").show().find("select, input, textarea").attr('disabled',false);
		$("#email").show().find("select, input, textarea").attr('disabled',false);
		$("#reference").show().find("select, input, textarea").attr('disabled',true);
	}
	lastData = $form.serializeArray();
});

//Cancel button functionality while adding a funder
function cancel(serviceAppId,businessAppId,section,subSection,elementId){
	var $self=$(this);
	var $form = $("#contractTypeDivId").closest('form');
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
					var elementCount = document.myformContract.elements.length;
					document.myformContract.action = document.myformContract.action+'&service_app_id='+serviceAppId+'&business_app_id='+businessAppId+'&next_action=cancelContract&section='+section+"&subsection="+subSection+"&elementId="+elementId;
					document.myformContract.submit();
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
		var elementCount = document.myformContract.elements.length;
		document.myformContract.action = document.myformContract.action+'&service_app_id='+serviceAppId+'&business_app_id='+businessAppId+'&next_action=cancelContract&section='+section+"&subsection="+subSection+"&elementId="+elementId;
		document.myformContract.submit();
	}
	
	
}
//Select from previously added Contracts/Grants
function setSelectionValue(obj,serviceAppId,businessAppId,section,subSection,elementId){
	var comboValue;
	var selectedValue = $(obj).val();
	var selIndex = document.myformContract.selectBox.selectedIndex;
	comboValue = document.myformContract.selectBox.options[selIndex].value;
	document.myformContract.action = document.myformContract.action+'&service_app_id='+serviceAppId+'&business_app_id='+businessAppId+'&next_action=selectValue&selectBoxValue='+selectedValue+"&section="+section+"&subsection="+subSection+"&elementId="+elementId;  
	document.myformContract.submit();
}        
//This method show/hide the div on the page for nyc/non-nyc type
function showMe (obj) {
	if($(obj).val()=='NYC Government'){
		$("#nycAgency").show().find("select, input, textarea").attr('disabled',false);
		$("#funderName").hide().find("select, input, textarea").attr('disabled',true);
		$("#firstName").hide().find("select, input, textarea").attr('disabled',true);
		$("#midName").hide().find("select, input, textarea").attr('disabled',true);
		$("#lastName").hide().find("select, input, textarea").attr('disabled',true);
		$("#title").hide().find("select, input, textarea").attr('disabled',true);
		$("#phone").hide().find("select, input, textarea").attr('disabled',true);
		$("#email").hide().find("select, input, textarea").attr('disabled',true);
		$("#reference").hide().find("select, input, textarea").attr('disabled',true);
	}else{
		$("#nycAgency").hide().find("select, input, textarea").attr('disabled',true);
		$("#funderName").show().find("select, input, textarea").attr('disabled',false);
		$("#firstName").show().find("select, input, textarea").attr('disabled',false);
		$("#midName").show().find("select, input, textarea").attr('disabled',false);
		$("#lastName").show().find("select, input, textarea").attr('disabled',false);
		$("#title").show().find("select, input, textarea").attr('disabled',false);
		$("#phone").show().find("select, input, textarea").attr('disabled',false);
		$("#email").show().find("select, input, textarea").attr('disabled',false);
		$("#reference").show().find("select, input, textarea").attr('disabled',true);
	}
} 

//Below function is added to fix defect 4453 as part of release 2.7.0
$.validator.addMethod("textArea_Maxlength", function( value, element, param ) {
	var lbmaxlength = true;
    if(value.length + value.split("\n").length > 251){
    	lbmaxlength = false;
   }
    return lbmaxlength;
},"! Input should be less then 250 characters" );

$(document).ready(function() {
	//This method calls when page get load.
	// Fix for defect # 1774, Script to remove option from dropdown if
	//there is single option in Dropdown.
	if($('#selectBox').children('option').length == 1)
        $("#selectBox option[value='-1']").remove();
	//Fix for defect #1774 ends.
	
	$("input[type='text']").each(function(){
        if($(this).attr("validate")=='calender'){
              $(this).keypress(function(event) {
                    event = (event) ? event : window.event;
                    var charCode = (event.which) ? event.which : event.keyCode;
                    var isValid = isNumber(this,event);
               if(isValid){
                     if(charCode==8 || charCode==46){
                          return true; 
                     }else{
                           validateDate(this,event);
                     }
               }else{
                   return false;
               }
              });
              $(this).keyup(function(event) {
                    event = (event) ? event : window.event;
                    var charCode = (event.which) ? event.which : event.keyCode;
                    if($(this).val().length == 2 || $(this).val().length == 5) {
                          if(charCode==8 || charCode==46){
                                return true;
                          }else{
                                $(this).val($(this).val() + '/');
                          }
                    }
              });
              $(this).blur(function(event) {
                    verifyDate(this);
              });
        
        }
  });
	//perform the validation  like required field check, email check, maxlength check etc .on the funder screen.
	//Below function is modified to fix defect 4453 as part of release 2.7.0
$("#myformContract").validate({
			rules: {
				contractFunderName: {passSpecialChar: true,
									required: true,
									maxlength: 60,
									allowSpecialChar: ["A", ".,\\\'\\\" -"]},
				contractRefFirstName: {passSpecialChar: true,
									required: true,
									maxlength: 32,
									allowSpecialChar: ["A", ".,\\\'\\\" -"] },
				contractRefMidName: {
									maxlength: 1,
									allowSpecialChar: ["A", ".,\\\'\\\" -"] },
				contractRefLastName: {passSpecialChar: true,
								required: true,
								maxlength: 64,
								allowSpecialChar: ["A", ".,\\\'\\\" -"] },
				contractRefTitle: {noneSelected: true},
				contractType: {noneSelected: true},
				contractRefPhone: {required: true,
					patternMatcher:"NNN-NNN-NNNN"},
				contractRefEmail: {required: true,
							maxlength: 128,
							email:true},
				msContractId:{passSpecialChar: true,
							required: true,
							maxlength:30,
							minlength:2,
							allowSpecialChar: ["A", " !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7"]},
				contractDescription: {passSpecialChar: true,
									required: true,
									textArea_Maxlength: true,
									allowSpecialChar: ["A", " !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7 \n\r\t "]},
				contractStartDate: {required: true,
									date: true},
				contractEndDate: {required: true,
									date: true},
				contractBudget: {required: true,
								maxlength:19},
				contractNYCAgency:{noneSelected: true},
				
				nycAgency:{required: {
				depends: function(element) {
                    return ($("#contractType").val()=='NYC Government');
				}}},
				funderName:{required: {
				depends: function(element) {
                    return ($("#contractType").val()!='NYC Government');
				}}},
				firstName:{required: {
				depends: function(element) {
                    return ($("#contractType").val()!='NYC Government');
				}}},
				midName:{required: {
				depends: function(element) {
                    return ($("#contractType").val()!='NYC Government');
				}}},
				lastName:{required: {
				depends: function(element) {
                    return ($("#contractType").val()!='NYC Government');
				}}},
				title:{required: {
				depends: function(element) {
                    return ($("#contractType").val()!='NYC Government');
				}}},
				phone:{required: {
				depends: function(element) {
                    return ($("#contractType").val()!='NYC Government');
				}}},
				email:{required: {
				depends: function(element) {
                    return ($("#contractType").val()!='NYC Government');
				}}},
				reference:{required: {
				depends: function(element) {
                    return ($("#contractType").val()=='NYC Government');
				}}}
			},
			messages: {
				contractFunderName: {passSpecialChar: "! This field is required",
									required:"! This field is required.",
									maxlength: "! Input should be less then 60 characters",
									allowSpecialChar: "! Only , . - and spaces are allowed as a special character."},
				contractRefFirstName: {passSpecialChar: "! This field is required",
									required: "! This field is required.",
									maxlength: "! Input should be less then 32 characters",
									allowSpecialChar: "! Only , . - and spaces are allowed as a special character."} ,
				contractRefMidName:{
									maxlength: "! Middle name can have only 1 character",
									allowSpecialChar: "! Only , . - and spaces are allowed as a special character."},
				contractRefLastName: {passSpecialChar: "! This field is required",
									required: "! This field is required.",
									maxlength: "! Input should be less then 64 characters",
									allowSpecialChar: "! Only , . - and spaces are allowed as a special character."},
				contractRefTitle: {required: "! This field is required."},
				contractRefPhone: {required: "! This field is required."},
				contractRefEmail: {required: "! This field is required.",
								maxlength: "! Email should be less then 128 characters",
								email: "! Please enter a valid email address." },
				msContractId: {passSpecialChar: "! This field is required",
							required: "! This field is required.",
							maxlength:"! Input should be between 2-30 characters only",
							minlength:"! Input should be between 2-30 characters only",
							allowSpecialChar: "! Only , . - and spaces are allowed as a special character."},
				contractDescription: {passSpecialChar: "! This field is required",
									required: "! This field is required.",
									textArea_Maxlength: "! Input should be less then 250 characters",
									allowSpecialChar: "! Only ! @ # $ % ^ & * () - _ + = | \ { } [ ] ; : \" \' < > , . ? / ` ~ \xA7 and \n spaces are allowed as a special character."},
				contractStartDate: {required: "! This field is required.",
									date: "! Not a valid date."},
				contractEndDate: {required: "! This field is required.",
									date: "! Not a valid date."},
				contractBudget: {required: "! This field is required.",
					maxlength: "! Input should be less then 14 characters"}	
			},
			submitHandler: function(form){
				if(checkBudgetValue()){
					var selectedValue = $("#selectBox").val();
					
					var isValid = true;
					$("input[type='text']").each(function(){
				        if($(this).attr("validate")=='calender'){
				              if(!verifyDate(this)){
				            	  isValid = false;
				              }
				        }
				    });
					if(isValid){
						if(checkStartEndDate()){
							document.myformContract.action = document.myformContract.action+'&service_app_id='+$("#hiddenServiceAppId").val()+'&business_app_id='+$("#hiddenBusinessAppId").val()+'&next_action=save&selectBoxValue='+selectedValue+"&section="+$("#hiddenSectionId").val()+"&subsection="+$("#hiddenSubSectionId").val()+"&elementId="+$("#hiddenElementId").val();
							document.myformContract.submit();
							}
							else{
									$("#startDate").html("</br>! End Date can not be less than Start Date.");
								}
						} 
					} else {
						$("#budgetError").html("! Please enter a value less than $100,000,000,000.00");
					}
				
			},
			  errorPlacement: function(error, element) {
			         error.appendTo(element.parent().parent().find("span.error"));
			  }
		});
});
//This method will check that the given contract id is unique
function checkContractId(obj,serviceAppId,businessAppId,section,subSection,elementId,event){
	var selectedValue = $("#selectBox").val();
	var url = document.myformContract.action+'&service_app_id='+serviceAppId+'&business_app_id='+businessAppId+'&next_action=save&checkId=check&selectBoxValue='+selectedValue+'&section='+section+"&subsection="+subSection+"&elementId="+elementId;
	$("#checkContractIdHidden").val("checkContractId");
}
//This method check if the currency entered is valid or not.
function checkBudgetValue(){
	var budgetValue = $("#contractBudget").val();
	var budget = budgetValue.replaceAll(",","");
	if(parseFloat(budget)-100000000000.00 <= 0 ){
		return true;
	} else {
		return false;
	}
}
//Check if start date is less than end date.
function checkStartEndDate(){
	$("#checkContractIdHidden").val(null);
	var SDate = document.getElementById('contractStartDate').value;     
	var EDate =  document.getElementById('contractEndDate').value;
	var endDate = new Date(EDate);     
	var startDate= new Date(SDate);
	   if(SDate != '' && EDate != '' && startDate > endDate)
	{
	 return false;
	}  
	else{
		   return true;
	   }
}

//This method set the max length.
//updated in R5
function setMaxLength(obj,maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}
