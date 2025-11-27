suggestionVal = "";
var isvalid = false;
//This function is used to render the status of the screen on load
$(document).ready(function() 
		{
		
	$("#cancel").click(function() {
		clearAndCloseOverLay();
	});

	$("#addAssignee").click(function(){
		validateAndSubmitAssignee();
	});
		
		
				$('#txtVendorname').keyup(function() {
				
					if ($("#txtVendorname").val() != '') {
						$('#addAssignee').removeAttr("disabled");
					} else {
						$('#addAssignee').attr("disabled", "disabled");
					}
				});
			});
		
	//type head search for Vendor
	typeHeadSearch($('#txtVendorname'), $("#getVendorList").val(),"addAssignee",
			"typeHeadCallBack", $("#errorMsgInternal"));
			
	//THis function is called when USer selects Vendor name by AutoComplete
	function typeHeadCallBack() {
		var valueToSearch = $('#txtVendorname').val();
		var isValid = isAutoSuggestValid(valueToSearch, suggestionVal);
		if (!isvalid && $('#txtVendorname').val() != ''
				&& $('#txtVendorname').val().length > 3) {
			
			suggestionVal = "";
			$('#errorMsgInternal').html(
			"! Please select a registered vendor or register the proposed assignee with the Comptroller.");
		} else if ($('#txtVendorname').val().length <= 3) {
			$('#txtVendorname').parent().parent().next().find("span").html(
			"");
		}
		
	};
	//THis function is called when USer selects Vendor name by AutoComplete
	function typeHeadCallBackInternal() {
		
		if (!isvalid && $('#txtVendorname').val() != ''
				&& $('#txtVendorname').val().length > 3) {
			$(".autocomplete").html("").hide();
			suggestionVal = "";
			$('#errorMsgInternal')
					.html(
							"");
		} else if ($('#txtVendorname').val().length <= 3) {
			$('#errorMsgInternal').html("");
		}
	};
	
	//This function closes the Add Assignee overlay
	function clearAndCloseOverLay1() {
	$("#overlayDivId1").html("");
	$(".overlay").closeOverlay();
	}
	
//This function is called, when 'Add Assignee' button is clicked - it submits the form and close the overlay on success and display error message in case of failure
function submitAssignee(){
		pageGreyOut();
		
		var v_parameter = "&" + $("#addAssigneeVendorForm").serialize();
		
		var urlAppender = $("#addAssigneeSubmit").val();
		
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
					$("#accordionHeaderId3").click();
					$("#assignmentWrapper").html("");
					$("#accordionHeaderId3").click();
				}
				 removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				 removePageGreyOut();
			}
		});
}
	
//This method validates and submits the asssignee	
	function validateAndSubmitAssignee() 
{

	if (validateInvoiceSubmitForm()) {
		submitAssignee();
		return true;
	}
	return false;
}
// This method vaidates the form 	
	function validateInvoiceSubmitForm() {
		isProviderValid = true;
		
		if($('#providerId').val() == ''){
			
			$(".autocomplete").html("").hide();
			suggestionVal = "";
			$('#errorMsgInternal')
					.html(
							"! Please select a registered vendor or register the proposed assignee with the Comptroller.");
			isProviderValid = false;
			}
			
		return (isProviderValid);
	}