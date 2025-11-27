//This js file is used for the page renewContract.jsp

var suggestionVal = "";
var isValid = false;

// This will add the rule on date fields which will validate its format
$.validator.addMethod("date_validation", function( value, element, param ) {
			    return this.optional(element) 
			        || (callDateValidation(param));
			} );

//This will add the rule on date fields which will validate its range whether end date is priror to start date or not
$.validator.addMethod("inValidDate_Range", function( value, element, param ) {
    return this.optional(element) 
        || (compareDates(param));
} );

//This will add the rule on contract title fields which will validate for special characters
$.validator.addMethod("contractTitleValidation", function( value, element, param ) {
    return this.optional(element) 
        || (validateTextArea("contractTitlePopUp"));
});

$(document)
.ready(
		function() {
			$('#epin').alphanumeric({ nchars:"_"});
			putOnloadValidation();
			$('#contractValue').autoNumeric({
				vMax : '9999999999999999.99'
			});
			document.getElementById("searchepinbutton").disabled = true;
			typeHeadSearch($('#epin'), $("#getEpinListResourceUrl").val()+ "&epinQueryId=fetchRenewContractEpinList", "searchepinbutton", "typeHeadCallBack");
			$("#renewContractButton").click(function(){
				$("#ErrorDiv").hide();
				$("#transactionStatusDiv").hide();
			$("#renewContractForm").validate({
								onfocusout: false,
							    focusCleanup: true,
								focusInvalid: false,
	                            rules: {
	                            	   awardEpin:{required: true },
	                                   accProgramName:{required: true },
	                                   contractTitlePopUp:{required: true,contractTitleValidation:true  },
	                                   vendorFmsId:{required: true },
	                                   contractValue:{required: true },
	                                   contractEndDate:{required: true ,date_validation: ["contractEndDate"] },
	                                   contractStartDate:{required: true,date_validation: ["contractStartDate"], inValidDate_Range: ["contractStartDate", "contractEndDate"]}
	                                },
	                            messages: {
	                            	   awardEpin :{ required: requiredKey },
	                                   accProgramName:{ required: requiredKey },
	                                   contractTitlePopUp:{ required: requiredKey,contractTitleValidation:"! This Field contains invalid characters." },
	                                   vendorFmsId:{ required: requiredKey },
	                                   contractValue:{ required: requiredKey },
	                                   contractEndDate:{ required: requiredKey,date_validation:"" },
	                                   contractStartDate:{ required: requiredKey,date_validation:"",inValidDate_Range:"Invalid date range entered." }
	                                   
	                                   
	                                },
		
					//submit form once all validations are passed                                
		            submitHandler: function(form){
				
		            	submitRenewContract();
		       
					
					},errorPlacement: function(error, element) {
				         	error.appendTo(element.parent().parent().find("span.error"));
				  	  }
	            });
			  });
	});

//This method returns boolean after applying date format validation
function callDateValidation(param){
	
	var isvalid = true;
	isvalid = verifyDate(document.getElementById(param[0]));
    return isvalid;
}
	
//This method returns boolean after comparing start date and send date
function compareDates(param){
  var lbDateOK = true; // we assume start and end dates are in correct format
  var lsStartDate = document.getElementById(param[0]).value;
  var lsEndDate = document.getElementById(param[1]).value;
  var startDate = new Date(lsStartDate);
  var endDate = new Date(lsEndDate);
  if (endDate <= startDate)
	 {
	  lbDateOK=false;
	 }
 return lbDateOK;
}


//This function disables the search button in case no value is provided
function typeHeadCallBack() {
	var valueToSearch = $('#epin').val();
	var buttonIdToEnable = "searchepinbutton";
	var isValid = isAutoSuggestValid(valueToSearch, suggestionVal);
	var isValidComplete = isAutoSuggestValidComplete(valueToSearch, suggestionVal);
	if (buttonIdToEnable != null
			&& typeof (buttonIdToEnable) != "undefined") {
		if (isValidComplete) {
			document.getElementById(buttonIdToEnable).disabled = false;
		} else {
			document.getElementById(buttonIdToEnable).disabled = true;
		}
	}
	if (!isValid && $('#epin').val().length >= 3) {
		$(".autocomplete").html("").hide();
		suggestionVal = "";
		$('#epin').parent().parent().next().find("span").html(
				"! There are no E-PINs that match the entry.");
	} else if(isValid || $('#epin').val().length < 3){
		$('#epin').parent().parent().next().find("span").html("");
	}
}

//This method executes on click of Find Epin button to fetch Epin  based details
function findEpin(epinVal){
		pageGreyOut();
		var v_parameter = "epinValue=" + epinVal+"&contractId="+$("#hdnContractId").val();
		var urlAppender = $("#populateRenewContractPageUrl").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#overlayDivId").html(e);
				$(".overlay").launchOverlayNoClose($(".alert-box-amend-contract"),
						"850px", null, "onReady");
				$("a.exit-panel").click(function() {
					clearAndCloseOverLay();
				});
				 removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				 removePageGreyOut();
			}
		});
}
//This method executes on click of Renew Contract button
function submitRenewContract(){
		pageGreyOut();
	    document.getElementById("accProgramName").disabled=false;
		var v_parameter = "&" + $("#renewContractForm").serialize();
		var urlAppender = $("#RenewContractSubmitUrl").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(responseText) {
				document.getElementById("accProgramName").disabled=true;
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



	