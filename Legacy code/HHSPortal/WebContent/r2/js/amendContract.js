//This js file is used for the page amendContract.jsp

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

//[Start] R9.5.0 QC9615
$.validator.addMethod("inValidDate_StartDate", function(value, element, param) {
	//console.log('element= ' + $(element).attr('name') + ' param= ' + param +    '  value:' + value  );

    return this.optional(element) 
    || (compareConSrtAmdSrtDates(param));
} );
//[End] R9.5.0 QC9615
//[Start] R9.5.0 QC9657
$.validator.addMethod("inMaxLength", function(value, element, param) {
	//console.log('element= ' + $(element).attr('name') + ' param= ' + param +    '  value:' + value  );

    return this.optional(element) 
    || (validateDescritionLength(param));
}  );
//[End] R9.5.0 QC9657
/* on page load
Updated Method in R4*/
$(document)
.ready(
		function() {
			$('#epin').alphanumeric({ nchars:"_"});
			if($.isNumeric($.trim($('#contractValueTxt').val()))){
				$('#contractValueTxt').val(jqGridDelimitNumbers($('#contractValueTxt').val()));
			}
			
			putOnloadValidation();
			if($.isNumeric($.trim($('#contractValue').val()))){
				$('#contractValue').val(jqGridDelimitNumbers($('#contractValue').val()));
			}
			
			$('#amendValue').autoNumeric(
				{vMin: '-9999999999999999.99', vMax: '9999999999999999.99'}
			);

			//[Start] R9.5.0 QC9615
			if( $("#procDescription").val().length > 120 ){
			     document.getElementById('procDescription').readOnly = false;
			     //Please enter a value less than or equal to 120.
			}
			//[End] R9.5.0 QC9615


			document.getElementById("searchepinbutton").disabled = true;
			//typehead for EPin
			typeHeadSearch($('#epin'), $("#getEpinListResourceUrl").val()+ "&epinQueryId=fetchAmendContractEpinList", "searchepinbutton", "typeHeadCallBack");
			$("#amendContractButton").click(function(){
				$("#ErrorDiv").hide();
				$("#transactionStatusDiv").hide();
				//validation of amend contract form
				$("#amendContractForm").validate({
								onfocusout: false,
							    focusCleanup: true,
								focusInvalid: false,
	                            rules: {
	                            	   awardEpin:{required: true },
	                                   vendorFmsId:{required: true },
	                                   amendValue:{required: true },
	                                   amendmentReason:{required: true },
	                                   amendmentTitle:{required: true },  
		                               //[Start] R9.5.0 QC9615
	                                   procDescription:{  inMaxLength:["procDescription"]  }, 
		                               //[End] R9.5.0 QC9615  
	                                   proposedContractEnd:{required: true,date_validation: ["proposedContractEnd"], inValidDate_Range: ["contractStartDate", "proposedContractEnd"]},
	                                   amendmentEnd:{required: true ,date_validation: ["amendmentEnd"] },
		                               //[Start] R9.5.0 9657
	                                   amendmentStart:{required: true,date_validation: ["amendmentStart"], inValidDate_StartDate: ["amendmentStart", "amendmentEnd", "contractStartDate"]}
		                               //[End] R9.5.0 9657  
	                                },
	                            messages: {
	                            	   awardEpin :{ required: requiredKey },
	                            	   vendorFmsId:{ required: requiredKey },
	                            	   amendValue:{ required: requiredKey },
	                            	   amendmentReason:{ required: requiredKey },
	                            	   amendmentTitle:{ required: requiredKey },
	                            	 //[Start] R9.5.0 QC9615
	                            	   procDescription:{   inMaxLength:"! Please enter no more than 120 characters."  },
	                            	 //[End] R9.5.0 QC9615  
	                            	   proposedContractEnd:{ required: requiredKey,date_validation:"",inValidDate_Range:"Invalid date range entered." },
	                            	   amendmentEnd:{ required: requiredKey,date_validation:"" },
		                               //[Start] R9.5.0 QC9657
	                            	   amendmentStart:{ required: requiredKey,date_validation:"",inValidDate_StartDate:"Invalid date range entered." }
		                               //[End] R9.5.0 QC9657  
	                                },
					//submit form once all validations are passed 
		            submitHandler: function(form){
		            	submitAmendContract();
					},errorPlacement: function(error, element) {
				         	error.appendTo(element.parent().parent().find("span.error"));
				  	  }
	            });
			  });
	    						
	});



//This method return boolean after applying date format validation
function callDateValidation(param){
	
	var isvalid = true;
	isvalid = verifyDate(document.getElementById(param[0]));
    return isvalid;
}
	
//This method return boolean after comparing start date and send date
function compareDates(param){
  var lbDateOK = true; // we assume start and end dates are in correct format
  var lsStartDate = document.getElementById(param[0]).value;
  var lsEndDate = document.getElementById(param[1]).value;
  var startDate = new Date(lsStartDate);
  var endDate = new Date(lsEndDate);
  if (endDate < startDate)
	 {
	  lbDateOK=false;
	 }
 return lbDateOK;
}

//[Start] R9.5.0 9657
function compareConSrtAmdSrtDates(param){
	  var lbDateOK = true;
	  var lsAmdStartDate = document.getElementById(param[0]).value;
	  var lsAmdEndDate = document.getElementById(param[1]).value;
	  var lsContractStartDate = document.getElementById(param[2]).value;
	  var amdStartDate = new Date(lsAmdStartDate);
	  var amdEndDate = new Date(lsAmdEndDate);
	  var contractStartDate = new Date(lsContractStartDate);
	  if (amdEndDate < amdStartDate) {
		  lbDateOK=false;
	  }
	  if ( amdStartDate  < contractStartDate  ) {
		  lbDateOK=false;
	  }

	 return lbDateOK;
}



//This function validate APT Procurement Description length less then 120 
function validateDescritionLength(param){
	var isvalid = true;
	var descLengh  =  document.getElementById(param).value;
	if( descLengh.length > 120 ){
	     isvalid = false;
	} 
	return isvalid;
}
//[End] R9.5.0 QC9615



//This method executes on click of Find Epin button to fetch Epin  based details
function findEpin(epinVal){
	
		pageGreyOut();
		/* R6: Added extra request parameter to save for epin validation*/
		var v_parameter = "epinValue=" + epinVal+"&contractId="+$("#hdnContractId").val()+"&contractAgencyId="+$("#contractAgencyId").val();
		var urlAppender = $("#populateAmendContractPageUrl").val();
		
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

//This function disable the search button in case no value is provided
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


/*This method executes on click of Amend Contract button
Updated Method in R4*/
function submitAmendContract(){
		pageGreyOut();
		
		var v_parameter = "&" + $("#amendContractForm").serialize();
		var urlAppender = $("#AmendContractSubmitUrl").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(result) {				
				if (result["error"] == 0) {
					window.location.href = $("#duplicateRender").val();
					$("#successGlobalMsg").show();
					$("#successGlobalMsg").html(result["message"]);
					clearAndCloseOverLay();
				} else if (result["error"] == 1)
				{
					$("#ErrorDiv").show();
					$("#ErrorDiv").html(result["message"]);
				} else if (result["error"] == 2)
				{
					$("#errorGlobalMsg").show();
					$("#errorGlobalMsg").html(result["message"]);		
					clearAndCloseOverLay();
				}
				removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				 removePageGreyOut();
			}
		});
}

/*This method compares two dates
Updated Method in R4*/
function compareTwoDates(aDate1, aDate2)
{
	date1 = new Date(aDate1);
	date2 = new Date(aDate2);
	
	if(date1>date2) {
		return 1;
	}
	if(date1<date2) {
		return -1;
	}
	return 0;
}


	