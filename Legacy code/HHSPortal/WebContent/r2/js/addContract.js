//This js file is used for the page addContract.jsp

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
			$("#accProgramName").prop('disabled', true);
			$("#agencyId").change(function() {
				agency = $(this).prop("selectedIndex");
				if (agency == 0) {
					$("#accProgramName").val("");
					$("#accProgramName").prop('disabled', true);
				} else {
					getProgramNameList();
				}
			});
			putOnloadValidation();
			$('#contractValue').autoNumeric({
				vMax : '9999999999999999.99'
			});
			document.getElementById("searchepinbutton").disabled = true;
			typeHeadSearch($('#epin'), $("#getEpinListResourceUrl").val()+ "&epinQueryId=fetchAddContractEpinList", "searchepinbutton", "typeHeadCallBack");
			$("#addContractButton").click(function(){	
				$("#ErrorDiv").hide();
				$("#transactionStatusDiv").hide();
			$("#addContractForm").validate({
								onfocusout: false,
							    focusCleanup: true,
								focusInvalid: false,
	                            rules: {
	                            	   awardEpin:{required: true },
	                            	   accProgramName:{required: true },
	                            	   agencyId:{required: true },
	                                   contractTitlePopUp:{required: true,contractTitleValidation:true },
	                                   vendorFmsId:{required: true },
	                                   contractValue:{required: true },
	                                   contractEndDate:{required: true ,date_validation: ["contractEndDate"] },
	                                   contractStartDate:{required: true,date_validation: ["contractStartDate"], inValidDate_Range: ["contractStartDate", "contractEndDate"]}
	                                },
	                            messages: {
	                            	   awardEpin :{ required: requiredKey },
	                            	   accProgramName:{ required: requiredKey },
	                            	   agencyId:{ required: requiredKey },
	                                   contractTitlePopUp:{ required: requiredKey,contractTitleValidation:"! This Field contains invalid characters." },
	                                   vendorFmsId:{ required: requiredKey },
	                                   contractValue:{ required: requiredKey },
	                                   contractEndDate:{ required: requiredKey,date_validation:"" },
	                                   contractStartDate:{ required: requiredKey,date_validation:"",inValidDate_Range:"Invalid date range entered." }
	                                   
	                                   
	                                },
		
					//submit form once all validations are passed                                
		            submitHandler: function(form){
		            	
		            	//[Start] change for QC9398 during R8.3.0 
			            	//added check for start budget year, build 3.1.0, enhancement id 6020
			            	//Changes done for build 3.12.0, enhancement ID 6580
			            	//var contractStartFiscalYear  = getFiscalYear($('#contractStartDate').val());
	                        //var contractEndFiscalYear = getFiscalYear($('#contractEndDate').val());
		            	var contractStartDate  = $('#contractStartDate').val();
                        var contractEndDate = $('#contractEndDate').val();
                        
		        		var v_parameter = "&contractStartDate=" + contractStartDate + "&contractEndDate=" + contractEndDate ;

                        //[End] change for QC9398 during R8.3.0

		        		var urlAppender = $("#confirmFiscalYearUrl").val();
		        		jQuery.ajax({
		        			type : "POST",
		        			url : urlAppender,
		        			data : v_parameter,
		        			success : function(e) {
		        				var splitArray = e.split(",");
		        				if(splitArray!=''){
		        					var select = document.getElementById("fiscalYearId");
		        					$('#fiscalYearId option').remove();
		        					//select.appendChild("Select FY");
		        					for (var i = 0; i < splitArray.length; i++) {
		        					    var option = document.createElement("option");
		        					    select.options.add(option);
		        					    option.value = splitArray[i];
		        					    option.innerHTML = splitArray[i];
		        					    //$('#fiscalYearId').css('display', 'inline'); 
		        					}

		        				 $(".deleteOverlay").launchOverlay($(".alert-box-confirmFiscalYear"), $(".exitYesNo-panel"), "400px", null, "onReady"); 
		        				}
		        				else{
		                        	submitAddContract('');
		                        }
		        				 removePageGreyOut();
		        			},
		        			error : function(data, textStatus, errorThrown) {
		        				showErrorMessagePopup();
		        				 removePageGreyOut();
		        			}
		        		});
					},errorPlacement: function(error, element) {
				         	error.appendTo(element.parent().parent().find("span.error"));
				  	  }
	            });
			 });
			//Start: Changes for R6 APT interface epin change
			$('#agencyId').on('change', function() {
				if ($('#agencyId').val() != $("#typeAheadAgencyId").val()&& $("#typeAheadAgencyId").val()!='')
				{
					$('#agencyId').parent().parent().find('span.error').text('Note: EPIN is mapped to a different agency than you have selected.');
					$('#agencyId').parent().parent().find('span.error').show().attr("style","width: 44% !important;");
				}
				else 
				{
					$('#agencyId').parent().parent().find('span.error').hide();
				}
				});
			//End: Changes for R6 APT interface epin change					
	});

//Added new method as part of build 3.1.0, enhancement 6020
//This method returns fiscal year, with inputs as date parameter 
function getFiscalYear(passedDate){
    var dateSplitArray = passedDate.split("/");
  var fiscalYear=dateSplitArray[2];
  var fiscalMonth=dateSplitArray[0];
  if (fiscalMonth < 7)
    {
      fiscalYear = fiscalYear;
    }
    else {
          fiscalYear = parseInt(fiscalYear) + 1;
    }
  return fiscalYear;
}


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
  if (endDate <= startDate)
	 {
	  lbDateOK=false;
	 }
 return lbDateOK;
}

//This method executes on click of Find Epin button to fetch Epin  based details
function findEpin(epinVal){
		pageGreyOut();
		var v_parameter = "epinValue=" + epinVal;
		var urlAppender = $("#populateAddContractPageUrl").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#overlayDivId").html(e);
				//R6 change start: Updating the epin input value after page re render
				$("#epin").val(epinVal);
				$("#typeAheadAgencyId").val(epinVal.split('-')[1].trim());
				//R6:Changes end
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

//This method is changed as part of build 3.1.0, enhancement ID 6020
//This method is changed as part of build 3.12.0, enhancement ID 6580 
//This method executes on click of Add Contract button
function submitAddContract(passedValue){
		pageGreyOut();
        //Build 3.1.0, enhancement 6020
		//added a check to pass current year or next fiscal year as budget start year
		$("#nextFiscalYearValue").val('');
	         if(passedValue!=''){
	        	 	$(".deleteOverlay").closeOverlay();
		            $(".deleteOverlay").hide();
	        		var selectedValue = $('#fiscalYearId option:selected').val();
	        			if(selectedValue!='' && selectedValue != '-1'){
	      	             $("#nextFiscalYearValue").val(selectedValue);
	      	            }
	            }
	      
	      
		var v_parameter = "&" + $("#addContractForm").serialize();
		var urlAppender = $("#addContractSubmitUrl").val();
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
	
//This funtion fetches program name list depending upon agency selected by user
function getProgramNameList() {
	var url = $("#getProgramListForAgency").val() + "&agencyId="
			+ $("#agencyId").val();
	var jqxhr = $.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(data) {
			$("#accProgramName").html(data);
			$("#accProgramName").prop('disabled', false);
		},
		error : function(data, textStatus, errorThrown) {
		}
	});
}

//This funtion added for build 3.12.0, enhancement ID 6580
function closeCongifureFYOverlay(){
	 $(".deleteOverlay").closeOverlay();
     $(".deleteOverlay").hide();
}