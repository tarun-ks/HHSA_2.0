/**
 * This function is used to submit the form
 */
function onSubmit(){	
 	document.applicationSummaryForm.submit();
}

// display question page inside basic
function callController(serviceType,status,applicationId){	
	document.applicationSummaryForm.submit();
}
/**
 * This function is used to set the visibility for the tooltip
 * @param id id of the tool tip
 * @param visibility show and hide
 */
function setVisibility(id, visibility){
	document.getElementById(id).style.display = visibility;
}
/**
 * This function is used to set the value when terms and condition button is clicked
 * @param value value
 * @param process process
 */
function displayTermsAndCondition(value,process){
	$("#termsCondition").val(value);
	if(process!='' && process=='newApplicationProcess'){
		$("#newApplicationProcess").val(process);
	}
	document.applicationSummaryForm.submit();
}

// code start for pop up window to update the status
function closePopup(){
	$(".alert-box").hide();
	$(".overlay").hide();
	$("select").each(function() {
		document.getElementById($(this).attr("id")).selectedIndex = "";
	});
	$("#applicationType").val("");
	$("#viewHistory").val("");
}
/**
 * This function is used to check the max length for the text area
 * @param obj request obj
 * @param maxlimit max length limit
 * updated in R5
 */
function setMaxLength(obj,maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}
/**
 * This function is used to open the suspend and conditionally approve window
 * @param obj request object
 * @param applicationType application type whether business and service
 * @param oldStatus old status 
 * @param serviceAppID service application id
 * @param serviceApplicationName service application name 
 * @param workflowId work flow id
 * @param businessApplicationId business application id
 * @returns boolean true false
 */
function selectValue(obj,applicationType,oldStatus,serviceAppID,serviceApplicationName,workflowId,businessApplicationId){
	if("null" == workflowId)
	workflowId = "";
	$("#applicationType").val("");
	$("#newStatusValue").val("");
	$("#oldStatusValue").val("");
	$("#serviceElementId").val("");
	if(applicationType=='SERVICE ELEMENT ID'){
		$("#applicationType").val("Business");
		$("#workflowId").val(workflowId);
	}else{
		$("#applicationType").val("Service");
		$("#serviceAppID").val(serviceAppID);
		$("#serviceApplicationName").val(serviceApplicationName);
		$("#workflowId").val(workflowId);
	}	
	if(businessApplicationId!='' && businessApplicationId.length>0){
		$("#addButtonBusinessId").val(businessApplicationId)
	}
	var selectBoxId = $(obj).attr("id");
	var selectBoxText = obj.options[obj.selectedIndex].text;
	//set the new status
	
	$("#oldStatusValue").val(oldStatus);
	$("#serviceElementId").val(applicationType);
	
	var selectedValue = $(obj).attr('value');
	if(selectedValue!=-1){
		$(".overlay").launchOverlay($(".alert-box"), $(".exit-panel"), null, null, null);
	   	$("a.exit-panel").click(function(){
	   		$(".overlay").closeOverlay();
			$("select").each(function() {
				document.getElementById($(this).attr("id")).selectedIndex = "";
			});
			$("#applicationType").val("");
		});		  
	}
	$("#popupHeader").html("");
	$("#headerText").html("");
	$("#textBoxId").val("");
	$("#errorMessage").html("");
	$("#commentsId").show();
	
	if(selectedValue==1){
		$("#displayshared").empty();
		$("#newStatusValue").val("Suspended");
		$("#submitButtonId").val("Suspend");
		$("#submitButtonId").attr("title","Suspend");
		$("#popupHeader").append("Suspend");
		$("#headerText").append("Please enter any internal comments associated with this suspension. Only the HHS Accelerator team will be able to read this comment:");
	}else if(selectedValue==2){
		$("#displayshared").empty();
		$("#newStatusValue").val("Conditionally Approved");
		$("#submitButtonId").val("Conditionally Approve");
		$("#submitButtonId").attr("title","Conditionally Approve");
		$("#popupHeader").append("Conditional Approval");	
		$("#headerText").append("Please enter any internal comments associated with this conditional approval. Only the HHS Accelerator team will be able to read this comment:");
	}else if(selectedValue==3){
		$("#popupHeader").append("View History");
		$("#commentsId").hide();
		$("#historyType").val($("#applicationType").val());
		$("#applicationType").val("");
		$("#viewHistory").val("viewHistory");
		$("#displayshared").empty();
			var options={	
				   success: function(responseText, statusText, xhr){
				   		var $response=$(responseText);
                        var data = $response.contents().find(".overlaycontent1");
						$("#displayshared").show();
						$("#displayshared").empty();
						$("#displayshared").html(data);
						$(".overlay").launchOverlay($(".alert-box"), $(".exit-panel"), "750px");
					},
					error:function (xhr, ajaxOptions, thrownError){                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.applicationSummaryForm).ajaxSubmit(options);
				return false;	
	}
}
/**
 * This function is used to validate the input before submitting the suspend and conditionally approve request
 * @param obj input object
 * @returns boolean true and false
 */
function submitRequest(obj){
	$("#errorMessage").html("");
	if($("#textBoxId").val()=='' || $("#textBoxId").val==' '){
		$("#errorMessage").html("! This field is required");
		$("#textBoxId").focus();
		return false;
	}else{
		convertSpecialCharactersHTMLGlobal('textBoxId',true);
		lsResult =  "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7\n\r\t]+$";
		var re = new RegExp(lsResult);
        if(!re.test($("#textBoxId").val())){
            $("#errorMessage").html("! Your response contains invalid characters. Please re-type your response or visit Online Help for instructions on how to Copy and Paste as plain text.");
			$("#textBoxId").focus();
			return false;
		}else{
			$("#submitButtonId").attr("disabled","disabled");
			document.applicationSummaryForm.submit();
		}
	}
}

/**
 * This function is used after loading the page and display the alternate color for the table grid
 */
$(function(){
	var tableId= $("table[id='completeListId']");
	var selectBox = $("table[id='completeListId']").find("select");
	if(selectBox.length==0){
		$("#historyAction").hide();
	}
	if(tableId!=''){
		var allChild = $(tableId).find("tr");
		$(allChild).each(function(i) {
			if(i>0){
				if(i%2==0){
					$(this).removeClass();
					$(this).addClass("alternate");
				}else{
					$(this).removeClass();
				}
			}
		});
	}
});