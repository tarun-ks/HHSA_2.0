/**
 * This method called when page is getting loaded
 *  Updated Method in R4*/ 
$(document).ready(function() {

				$("#doNotCancel").click(function(e) {
					overlayLaunched.closeOverlay(e);
				});

				$(".awardEPIN-exit").click(function(e) {
					if (overlayLaunched != null) {
						e.stopPropagation();
						overlayLaunched.closeOverlay(e);
						resetEpinOverLayVals();
					}
				});
				$(".awardAmt").each(function() {
					$(".awardAmt").autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
				});
				//validates Award Epin form
				$("#assignAwardEpinForm").validate(
						{
							rules : {
								contractStartDate : {
									required : true,
									minlength : 10,
									maxlength : 10,
									DateFormat : true,
									DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50)),
									calenderFutureDate : true
								},
								contractEndDate : {
									required : true,
									minlength : 10,
									maxlength : 10,
									DateFormat : true,
									DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50)),
									calenderFutureDate : true,
									DateToFrom : new Array("contractStartDate", false)
								}
							},
							messages : {
								contractStartDate : {
									required : "! This field is required",
									minlength : "! Please enter a valid date", 	
									maxlength : "! Please enter a valid date",
									DateFormat : "! Please enter a valid date",
									DateRange : "! Invalid Date. Please enter a year equal to or after 1800.",
									calenderFutureDate : "! This date must be in the future"
								},
								contractEndDate : {
									required : "! This field is required",
									minlength : "! Please enter a valid date", 	
									maxlength : "! Please enter a valid date",
									DateFormat : "! Please enter a valid date",
									DateRange : "! Invalid Date. Please enter a year equal to or after 1800.",
									calenderFutureDate : "! This date must be in the future",
									DateToFrom : "! The end date must be after the start date"
								}
							},
							submitHandler : function(form) {
								/* R6: updatd the submit handler for epin validation
								 * Now first we are checking if the epin is valid, and then performing
								 * the epin assignment to the award*/
								assignAwardEpin();
				        		/* R6: changes ends*/
								
							},
							errorPlacement : function(error, element) {
								error.appendTo(element.parent().parent().find("span.error"));
								if(BrowserDetect.browser == 'Explorer' && BrowserDetect.version==7){
									var errorObject = $(".row").find("span >label.error");
									$(errorObject).each(function(){
										if($(this).html()!='' && $(this).html()!='*'){
											$(this).parent().parent().parent().removeAttr("style");
										}
									});
								}
							}
						});
				
});

/** R6: Now we are calling the epin validation ajax and then submitting the 
 * form if the epin is unique
 * 
 * updated the submit handler for epin validation
 * Now first we are checking if the epin is valid, and then performing
 * the epin assignment to the award*/
function assignAwardEpin()
{
	event.preventDefault(); // this will prevent the submit event.
	$("#messagediv").find("#failedMessage").html(""); // Emptying the error message
	$("#messagediv").hide(); //Hiding the previous error message on click of submit
	var procurementAgencyId = $("#procurementAgencyId").val();
	var awardEpinId = $("#awardEpinId").val();
	var urlAppender = $("#validateAwardEpinUrl").val();
	var v_parameter = "&procurementAgencyId=" + procurementAgencyId + "&awardEpinId=" + awardEpinId ;
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(response) {
			if(response.isValid!=undefined && response.isValid== "true")
			{
				document.assignAwardEpinForm.submit();
				removePageGreyOut();
			}
			else
			{
				$("#messagediv").find("#failedMessage").html(response.errorMessage);
				$("#messagediv").show();
				removePageGreyOut();
			}
			
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
	/* R6: changes ends*/
}

/* This method will be called when user select any of the radio button on add document screen
New Method in R4*/
function radioSelectValue(awardEpinId)
{
	$("#awardEpinId").val(awardEpinId);
	$("#assignEPIN").removeAttr("disabled");
}

/* R6: Non apt epins - storing the ref_apt_epin_id and procurement agencyId value on radio button change */
function radioSelectValue(epin, refAptEpinId, procurementAgencyId)
{
	$("#awardEpinId").val(epin);
	$("#procurementAgencyId").val(procurementAgencyId);
	$("#refAptEpinId").val(refAptEpinId);
	$("#assignEPIN").removeAttr("disabled");
}
