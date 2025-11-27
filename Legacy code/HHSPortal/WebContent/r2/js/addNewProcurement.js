/**
 * This js contains methods for psr work flow , type ahead to display the epin 
 *  and Function for launching PCOF task by city user.
 **/
var suggestionVal = "";
var isValid = false;
// Start Updated in R5
var previousEstAmount = 0, previousContractStartDate = '', previousContractEndDate='';
// End Updated in R5
/**
 *  Javascript for filter popup
 * */
function setVisibility(id, visibility) {
	document.getElementById(id).style.display = visibility;
}
/** 
 * This method called when page is getting loaded and set the values
 *  Updated Method in R4
 **/
$(document)
		.ready(
				function() {
				/**
				 * Start of changes for release 3.2.0 enhancement 5684 : 'Generate PCOF Task' button disabled once the task is generated
				 **/
					if(taskLaunch== "true" && generatedPSRFlag != "1"){//Updated in R5
						$("#generatePCoFTask").attr("disabled","disabled");
					}
				// End of changes for release 3.2.0 enhancement 5684
					var selectedUser = $("#accPrimaryContact").val();
					if (selectedUser == '') {
						$("#accSecondaryContact").val('');
						$("#accSecondaryContact").prop(
								'disabled', true);
					} else {
						removeOptionFromSecondDropDown('accPrimaryContact', 'accSecondaryContact');
					// Start || Changes made for enhancement #5688 for Release 3.2.0
					}
					var selectedUserAgency = $("#agecncyPrimaryContact").val();
					if (selectedUserAgency == '') {
						$("#agecncySecondaryContact").val('');
						$("#agecncySecondaryContact").prop(
								'disabled', true);
					} else {
						removeOptionFromSecondDropDown('agecncyPrimaryContact', 'agecncySecondaryContact');
					}
					// End || Changes made for enhancement #5688 for Release 3.2.0
					
					//hides the field for open ended RFPs
					previousEstAmount = $("#estProcurementValue").val();
					//changes for R5 start
					previousContractStartDate = $("#contractStartDateUpdated").val();
					previousContractEndDate = $("#contractEndDateUpdated").val();
					//changes for R5 ends
					if($("#isOpenEndedRFP").val()=='1'){
						$(".openEndedHide").hide();
						$("#estProcurementValue").val("0").parent().parent().hide();
						$("#estNumberOfContracts").val("0").parent().parent().hide();
					}else{
						$(".openEndedHide").show();
					}//hides/shows the fields for open ended RFPs
					$("#isOpenEndedRFP").change(function(){
						if($(this).val()=='1'){
							$(".openEndedHide").hide();
							$(".openEndedHide input[validate='calenderFormat']").val("");
							$(".openEndedHide label.error").hide();
							$("#estProcurementValue").val("0").parent().parent().hide();
							$("#estNumberOfContracts").val("0").parent().parent().hide();
						}else{
							$(".openEndedHide").show();
							$("#estProcurementValue").val("").parent().parent().show();
							$("#estNumberOfContracts").val("").parent().parent().show();
						}
					});
					$('#estProcurementValue').autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
					//cancel Procurement
					$("#cancelProcurementLink").click(function() {
						cancelProcurement("","");
					});//close procurement
					$("#closeProcurementLink").click(function() {
						closeProcurement("","");
					});
					var serviceUnitRequired = $("#serviceUnitHiddenFlag").val();
					if(null == serviceUnitRequired || '' == serviceUnitRequired){
						$("#serviceUnitRequired").attr('checked', true);
						$("#serviceUnitRequired").val('1');
					}
					else if(serviceUnitRequired == '1'){
						$("#serviceUnitRequired").attr('checked', true);
					}
					else{
						$("#serviceUnitRequired").attr('checked', false);
					}
					var procurementStatus = $("#procurementStatus").val();
					if(procurementStatus != '' && procurementStatus != '1' && procurementStatus != '2'){
						$("#serviceUnitRequired").attr("disabled", "disabled");
					}
					if(procurementStatus != '' && procurementStatus != '1'){
						$("#isOpenEndedRFP").attr("disabled", "disabled");
					}
					var pageW = $(document).width();
					var pageH = $(document).height();
					$(".alert-box-link-to-vault").hide();
					//on click of 'x' from overlay
			   		$("a.exit-panel").click(function(){					
					$(".addContract").hide();
					$(".overlay").hide();
					});	
			   		
					var pageW = $(document).width();
					var pageH = $(document).height();
					$(".alert-box-link-to-vault").hide();
						$("a.exit-panel").click(function(){					
					$(".addContract").hide();
					$(".overlay").hide();
					});	
					//disables the field
					$("#displayErrorMsg").val("");
					var orgType = $("#org_type").val();
					if (orgType == 'agency_org') {
						$(".agencyClass").prop('readonly', true);
						$(".agencyClass").attr("readonly", "readonly");
						$(".agencyClassDropDown").attr("disabled", "disabled");
						$(".agencyClassButton").attr("disabled", "disabled");
					}
					if ($("#screenReadOnly").val() == 'true') {
						$('#addProcurementform select').attr('disabled', 'disabled');
						$('#addProcurementform input').attr('disabled', 'disabled');
						$('#addProcurementform textarea').attr('disabled', 'disabled');
					}
					var procEPin = $("#procurementEpin").val();
					$("#procStatus").prop('readonly', true);
					$("#procurementEpin").prop('readonly', true);
					if (procEPin != 'Pending') {
						$("#assignepin").prop('disabled', true);
					}
					$(".ValidationError").css("color", "red");
					var agency = $("#agencyId").val();
					var selectedAccUser = $("#accPrimaryContact").val();
					// Updated for Emergency build : 4.0.0.2 defect 8383
					if($("#procurementDescription").attr("autosavepopulated") == null 
							|| typeof $("#procurementDescription").attr("autosavepopulated") == "undefined")
					{
						var procurementDescription = $("#procDescription").val();
						$("#procurementDescription").val(procurementDescription);
						$("#procurementDescription").trigger("paste");
					} 
					var selectedAgencyUser = $("#agecncyPrimaryContact").val();
					//checks for empty values in the form 
					if (agency == '') {
						$("#programNameId").attr('disabled', 'disabled');
						$("#agecncyPrimaryContact").val("");
						$("#agecncyPrimaryContact").attr('disabled', 'disabled');						
					}
					if (selectedAccUser == '') {
						$("#accSecondaryContact").attr('disabled', 'disabled');
					}
					if (selectedAgencyUser == '') {
						$("#agecncySecondaryContact").attr('disabled', 'disabled');
					}
					var status = $("#procurementStatusId").val();
					if(!($("#lbProcurementStatusNotDraft").val() == 'true' || $("#lbProcurementStatusDraft").val() == 'true')){
						$("#rfpReleaseDateUpdated").attr('disabled', 'disabled');
					}
					if(!($("#submissionCloseDateId").val() == null || $("#submissionCloseDateId").val() == '')){
						$("#proposalDueDateToId").attr('disabled', 'disabled');
					}
					var draftStatus = $("#lbProcurementStatusDraft").val();
					var notDraftStatus = $("#lbProcurementStatusNotDraft").val();
					if (draftStatus == 'true') {
						$(".draftStatus").attr('disabled', 'disabled');
					} else {
						$("#agencyId").attr('disabled', 'disabled');
						$(".StatusOtherThanDraft").attr('disabled', 'disabled');
					}
					if ((draftStatus == 'false') && (notDraftStatus == 'false')) {
						$("#procurementTitle").prop('readonly', true);
					}
					// on click of save button, make all the client side validation
					//if no error exists then submit the page
					$("#save")
							.click(
									function() {
										$("#futureDateErrorId").remove();
										procEPin = $("#procurementEpin").val();
										if (procEPin != 'Pending') {
											$("#assignepin").prop('disabled',
													true);
										}
									});
					$("#agencyId").change(function() {
						agency = $("#agencyId").val();
						if (agency == '') {
							$("#programNameId").val("");
							$("#programNameId").attr('disabled', 'disabled');
							$("#agecncyPrimaryContact").val("");
							$("#agecncyPrimaryContact").attr('disabled', 'disabled');
							$("#agecncySecondaryContact").val("");
							$("#agecncySecondaryContact").attr('disabled', 'disabled');
						} else {
							  pageGreyOut();
							  $("#programNameId").prop( 'disabled', false);
							  var url = $("#getProgramListForAgency").val() + '&nycAgency=agency';
							  hhsAjaxRender(null, document.addProcurementform,'progDivId,agencyPrimaryId,agencySecId',url,"setDisabledSecondryContact");
								$("#agecncyPrimaryContact").val("");	
								$("#agecncyPrimaryContact").attr('disabled', false);
								$("#agecncySecondaryContact").val("");
								$("#agecncySecondaryContact").removeAttr("disabled");
						  }
					});
					// on change of accPrimaryContact dropdown, and 
					//accordingly enable or disable accSecondaryContact dropdown
					$("#accPrimaryContact")
							.change(
									function() {
										var selectedUser = $(
												"#accPrimaryContact").val();
										if (selectedUser == '') {
											$("#accSecondaryContact").val('');
											$("#accSecondaryContact").prop(
													'disabled', true);
										} else {
											removeOptionFromSecondDropDown('accPrimaryContact', 'accSecondaryContact');
											$("#accSecondaryContact").prop(
													'disabled', false);
										}
									});
					// on change of agecncyPrimaryContact dropdown, and 
					//accordingly enable or disable agecncySecondaryContact dropdown
					
					// This will execute when user click Find E-pin
					$('#assignepin').click(
							function() {
								pageGreyOut();
								document.getElementById("searchepinbutton").disabled = true;
								$(".overlay").launchOverlayNoClose($(".alert-box-assignEpin"), "450px", null);
								removePageGreyOut();
								return false;
							});
					$(".assignepin-exit").click(function(e) {
						if (overlayLaunched != null) {
							e.stopPropagation();
							overlayLaunched.closeOverlay(e);
							resetEpinOverLayVals();
						}
					});
					//performing client side validation
					//As part of build 2.6.0, enhancement 5402 replaced calenderFutureDate with calenderFutureDateBasedOnProcStts
					//in rfpReleaseDatePlanned, rfpReleaseDateUpdated, proposalDueDatePlanned, proposalDueDateUpdated  
					//contractStartDatePlanned, contractStartDateUpdated, contractEndDatePlanned
					//contractEndDateUpdated - removed validation calenderFutureDate
					$("#addProcurementform")
							.validate(
									{
										rules : {
											procurementTitle : {
												required : true,
												minlength : 5,
												maxlength : 120
											},
											agencyId : {
												noneSelected : true
											},
											programName : {
												noneSelected : true
											},
											accPrimaryContact : {
												noneSelected : true
											},
											accSecondaryContact : {
												noneSelected : true
											},
											agecncyPrimaryContact : {
												noneSelected : true
											},
											agecncySecondaryContact : {
												noneSelected : true
											},
											email : {
												email : true,
												required : true,
												maxlength : 60
											},
											procurementDescription : {
												required : true,
												maxlength : 3500
											},
											estNumberOfContracts : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												maxlength : 4,
												numberFormatField : true,
												minStrict : 0
											},
											estProcurementValue : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												maxStrict : 9999999999999999.99,
												maxlength : 24
											},
											linkToConceptReport : {
												isURL : true,
												maxlength : 250
											},
											rfpReleaseDatePlanned : {
												required : true,
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50)),
												calenderFutureDateBasedOnProcStts : [true, procurementStatus, $("#rfpReleaseDatePlanned").val()]
											},
											rfpReleaseDateUpdated : {
												required : true,
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50)),
												calenderFutureDateBasedOnProcStts : [true, procurementStatus, $("#rfpReleaseDatePlanned").val()]
											},
											preProposalConferenceDatePlanned : {
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											preProposalConferenceDateUpdated : {
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											proposalDueDatePlanned : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50)),
												calenderFutureDateBasedOnProcStts : [true, procurementStatus, $("#proposalDueDatePlanned").val()]
											},
											proposalDueDateUpdated : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50)),
												calenderFutureDateBasedOnProcStts : [true, procurementStatus, $("#proposalDueDatePlanned").val()]
											},
											firstRFPEvalDatePlanned : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											firstRFPEvalDateUpdated : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											finalRFPEvalDatePlanned : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											finalRFPEvalDateUpdated : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											evaluatorTrainingDatePlanned : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											evaluatorTrainingDateUpdated : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											firstEvalCompletionDatePlanned : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											firstEvalCompletionDateUpdated : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											finalEvalCompletionDatePlanned : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											finalEvalCompletionDateUpdated : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											awardSelectionDatePlanned : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											awardSelectionDateUpdated : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
											},
											contractStartDatePlanned : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50)),
												calenderFutureDateBasedOnProcStts : [true, procurementStatus, $("#contractStartDatePlanned").val()]
											},
											contractStartDateUpdated : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50)),
												calenderFutureDateBasedOnProcStts : [true, procurementStatus, $("#contractStartDatePlanned").val()]
											},
											contractEndDatePlanned : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50)),
												calenderFutureDateBasedOnProcStts : [true, procurementStatus, $("#contractEndDatePlanned").val()],
												DateToFrom : new Array("contractStartDatePlanned", false)
											},
											contractEndDateUpdated : {
												required : {depends: function(element) {return checkIfElementAvailable(element);}},
												minlength : 10,
												maxlength : 10,
												DateFormat : true,
												DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50)),
												DateToFrom : new Array("contractStartDateUpdated", false)
											}

										},
										messages : {
											procurementTitle : {
												required : "! This field is required",
												minlength : "! You must enter 5 or more characters",
												maxlength : "! Input should be less than 120 characters"
											},
											agencyId : {
												noneSelected : "! This field is required"
											},
											programName : {
												noneSelected : "! This field is required"
											},
											accPrimaryContact : {
												noneSelected : "! This field is required"
											},
											accSecondaryContact : {
												noneSelected : "! This field is required"
											},
											agecncyPrimaryContact : {
												noneSelected : "! This field is required"
											},
											agecncySecondaryContact : {
												noneSelected : "! This field is required"
											},
											email : {
												email : "! Invalid format. Agency Email Contact must contain an '@', and a '.'",
												required : "! This field is required",
												maxlength : "! Input should be less than 60 characters"
											},
											procurementDescription : {
												required : "! This field is required",
												maxlength : "! Input should be less than 3500 characters"
											},
											estNumberOfContracts : {
												required : "! This field is required",
												maxlength : "! Input should be less than or equal to 4 digits",
												minStrict : "! Value must be greater than 0",
												numberFormatField : "! Value must be numeric"
											},
											estProcurementValue : {
												required : "! This field is required",
												maxStrict : "! Please enter a value less than $10,000,000,000,000,000.00",
												maxlength : "! Input should be less than or equal to 18 digits"
												
											},
											linkToConceptReport : {
												isURL : "! Link must be formatted in http:// or https:// form.",
												maxlength : "! Input should be less than or equal to 250 digits"
											},
											rfpReleaseDatePlanned : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800.",
												calenderFutureDateBasedOnProcStts : "! This date must be in the future"
											},
											rfpReleaseDateUpdated : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800.",
												calenderFutureDateBasedOnProcStts : "! This date must be in the future"
											},
											preProposalConferenceDatePlanned : {
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											preProposalConferenceDateUpdated : {
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											proposalDueDatePlanned : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800.",
												calenderFutureDateBasedOnProcStts : "! This date must be in the future"
											},
											proposalDueDateUpdated : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800.",
												calenderFutureDateBasedOnProcStts : "! This date must be in the future"
											},
											firstRFPEvalDatePlanned : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											firstRFPEvalDateUpdated : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											finalRFPEvalDatePlanned : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											finalRFPEvalDateUpdated : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											evaluatorTrainingDatePlanned : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											evaluatorTrainingDateUpdated : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											firstEvalCompletionDatePlanned : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											firstEvalCompletionDateUpdated : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											finalEvalCompletionDatePlanned : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											finalEvalCompletionDateUpdated : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											awardSelectionDatePlanned : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											awardSelectionDateUpdated : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800."
											},
											contractStartDatePlanned : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800.",
												calenderFutureDateBasedOnProcStts : "! This date must be in the future"
											},
											contractStartDateUpdated : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800.",
												calenderFutureDateBasedOnProcStts : "! This date must be in the future"
											},
											contractEndDatePlanned : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800.",
												calenderFutureDateBasedOnProcStts : "! This date must be in the future",
												DateToFrom : "! The end date must be after the start date"
											},
											contractEndDateUpdated : {
												required : "! This field is required",
												minlength : "! Please enter a valid date", 	
												maxlength : "! Please enter a valid date",
												DateFormat : "! Please enter a valid date",
												DateRange : "! Invalid Date. Please enter a year equal to or after 1800.",
												DateToFrom : "! The end date must be after the start date"
											}
										},
										submitHandler : function(form) {
											// R5 changes start
											if(checkRulesForZeroValue()){
												pageGreyOut();
												if(checkConditionsOnPlannedStatus()){
													submitProcurementForm(null, null);
												}
											}
												// R5 changes ends										
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
					typeHeadSearch($('#epin'), $("#getEpinListResourceUrl")
							.val()+ "&epinQueryId=fetchEpinList", "searchepinbutton","typeHeadCallBack",$('#epinError'));
					// This will execute when user click Find E-pin
					// This will execute when Cancel button is clicked from
					$('#cancelSearchEpin').click(function() {
						$(".overlay").closeOverlay();
						resetEpinOverLayVals();
						return false;
					});
					// This will execute when Cancel button is clicked from
					$('#closesearchEpin').click(function() {
						$(".overlay").closeOverlay();
						resetEpinOverLayVals();
						return false;
					});
					
					$("input[type=text]").each(function() {
						if(($(this).attr("readonly")!=null && $(this).attr("readonly")=='readonly') || 
								($(this).attr("disabled")!=null && $(this).attr("disabled")=='disabled')){
							$(this).next().removeAttr("onclick");
						}
					});
				});
/**
 * This method is called to remove the extra spaces
 * */
function removeSpace(inputObject){
	$(inputObject).each(function(){
		$(this).blur(function(){
			if($(this).val()!='' && $(this).attr("disabled")!='disabled'){
				$(this).parent().parent().parent().attr("style","display:block");
			}
		});
	});
}
/**
 * call back function used after getting the procurement epin
 * from the database to display the epin or the error message if any.
 */
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
		$('#epin').parent().parent().next().html(
				"! There are no E-PIN Matching with the value entered by you.");
	} else if(isValid || $('#epin').val().length < 3){
		$('#epin').parent().parent().next().html("");
	}
}

/**
 * populate epin details on the overlay
 **/
function populateEpinDetails() {
	var ePinId = $("#epin").val();
	var url = $("#getEpinDetailsResourceUrl").val() + "&ePinId=" + ePinId;
	var jqxhr = $
			.ajax({
				url : url,
				type : 'POST',
				cache : false,
				success : function(data) {
					var dataJSON = $.parseJSON(data);
					if (dataJSON.epinDetails != null) {
						if(dataJSON.epinDetails[0].ErrorMessage == null){
						$(".searchepingclass1").html(
								dataJSON.epinDetails[0].EpinId);
						$(".searchepingclass2").html(
								dataJSON.epinDetails[0].ProcurementStartDate);
						$(".searchepingclass3").html(
								dataJSON.epinDetails[0].AgencyDiv);
						$(".searchepingclass4").html(
								dataJSON.epinDetails[0].AgencyId);
						$(".searchepingclass5").html(
								dataJSON.epinDetails[0].ProjProg);
						$(".searchepingclass6").html(
								dataJSON.epinDetails[0].Description);
						//R6: Setting the REF_EPIN_ID as input hidden to know which epin was selected
						$("#hiddenRefEpin").val(dataJSON.epinDetails[0].RefEpinId);
						
						document.getElementById("assignepinfinal").disabled = false;
						}else{
							$(".searchepingclassError").html(
									dataJSON.epinDetails[0].ErrorMessage);	
						}
					}
				},
				error : function(data, textStatus, errorThrown) {
					removePageGreyOut();
					showErrorMessagePopup();
				}
			});
}
/**
 * making ajax call on change of agency dropdown
 * fetching the program list linked to the selected agency
 */
function getProgramList() {
	pageGreyOut();
	var agencyId = $("#agencyId").val();
	if (agencyId == '') {
		$("#programNameId").val("");
		$("#programNameId").attr('disabled', 'disabled');	
		
	} else {
		var url = $("#getProgramListForAgency").val()
				+ "&next_action=getProgramList&agencyId=" + agencyId;
		var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(data) {
				//$.unblockUI();
				removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				removePageGreyOut();
				showErrorMessagePopup();
			}
		});
	}

}

/**
 * This method will set the epin value selected by user
 **/
function setEpinId() {
	var epinId = $(".searchepingclass1").html();
	$("#procurementEpin").val(epinId);
	$(".overlay").closeOverlay();
	resetEpinOverLayVals();
	return false;
}
/**
 * This method is added for R5 PSR workflow
 **/
function viewPsr()
{
	var procId = $('#procurementId').val();
	var psrDetailId = $('#psrDetailsId').val();
	var pdffilename = "PSR Form-";
	var procTitle = $('#procurementTitle').val();
	var procPdfTitle = procTitle.trim().replace(/[^a-zA-Z0-9_ ]+/gi, '');
	if(procPdfTitle.length > 50)
	{
		procPdfTitle = procPdfTitle.substr(0,50);
	}
	 pageGreyOut();
		window.open($("#contextPathSession").val() + "/GetContent.jsp?downloadPdf=downloadPdf" +"&procurementId=" + procId
			+ "&psrDetailsId=" + psrDetailId + "&documentName="+ pdffilename + procPdfTitle ); 
		removePageGreyOut();
}
/**
 * resetting epin details on overlay
 **/
function resetEpinOverLayVals() {
	$(".searchepingclass1").html("--");
	$(".searchepingclass2").html("--");
	$(".searchepingclass3").html("--");
	$(".searchepingclass4").html("--");
	$(".searchepingclass5").html("--");
	$(".searchepingclass6").html("--");
	$("#epin").val("");
	$('#epin').parent().parent().next().html("");
	suggestionVal = "";
	$(".autocomplete").html("").hide();
	document.getElementById("assignepinfinal").disabled = true;
}

/**
 * This function check the email format
 * 
 * @param inputvalue
 *            input value
 * @returns true false
 */
function checkEmail(inputvalue) {
	var pattern = /^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\.([a-zA-Z])+([a-zA-Z])+/;
	if (pattern.test(inputvalue)) {
		return true;
	} else {
		return false;
	}
}

/**
 * making ajax call to close the procurement
 **/
function closeProcurement(procurementTitle,selectElement)
{
	 pageGreyOut();
	   var urlAppender = $("#hiddencloseProcurementOverayContentUrl").val();
	   jQuery.ajax({
	   type: "POST",
	   url: urlAppender,
	   success: function(e){
		   	removePageGreyOut();
			$("#requestAdvance").html(e);
			$(".overlay").launchOverlay($(".alert-box-closeProcurement"), $(".close-procurement"), "600px", null); 
   		 },
  	   error : function(data, textStatus, errorThrown) {
    					showErrorMessagePopup();
						removePageGreyOut();
  	   }
	 });
}
/**
 * making ajax call to calcel the procurement
 **/
function cancelProcurement(procurementTitle,selectElement)
{
	 pageGreyOut();
	   var urlAppender = $("#hiddencancelProcurementOverayContentUrl").val();
	   jQuery.ajax({
	   type: "POST",
	   url: urlAppender,
	   success: function(e){
		   	removePageGreyOut();
			$("#requestCancel").html(e);
			$(".overlay").launchOverlay($(".alert-box-cancelProcurement"), $(".cancel-procurement"), "600px", null); 
   		 },
  	   error : function(data, textStatus, errorThrown) {
    					showErrorMessagePopup();
						removePageGreyOut();
  	   }
	 });
}
/**
 * This method id invoked on changing the primary contact and is used to enable/disable secondary contact
 **/
function callPrimaryContact(obj){
	var selectedUser = $("#agecncyPrimaryContact").val();
	if (selectedUser == '') {
		$("#agecncySecondaryContact").val('');
		$("#agecncySecondaryContact").prop(
				'disabled', true);
	} else {
		removeOptionFromSecondDropDown('agecncyPrimaryContact', 'agecncySecondaryContact');
		$("#agecncySecondaryContact").prop(
				'disabled', false);
	}
}
/**
 * This method is used to disable agecncySecondaryContact field
 * New Method in R4
 */
function setDisabledSecondryContact(){
	$("#agecncySecondaryContact").attr('disabled', 'disabled');
	removePageGreyOut();
}

/**
 * checks if element is available
 *  Updated Method in R4
 */
function checkIfElementAvailable(element){
	if(element == null || $(element).attr("id") == null || $(element).is(":visible") == false)
		return false;
	else
		return true;
}

/**
 * this function checks the rules for estimated values 
 **/
function checkRulesForZeroValue(){
	var currentEstAmount = $("#estProcurementValue").val();
	var procurementStatus = $("#procurementStatus").val();
	if(previousEstAmount != '0.00' && previousEstAmount != '0' && currentEstAmount == '0.00' && procurementStatus != '' && procurementStatus != '1'){
		$("#messagediverrorjs").show();
		$("#messagediverrorjs>div").html("Procurements cannot be changed to $0 after being published.");
		return false;
	}
	if((previousEstAmount == '0.00' || previousEstAmount == '0') && currentEstAmount != '0.00' && procurementStatus != '' && procurementStatus != '1' && procurementStatus != '2'){
		$("#messagediverrorjs").show();
		$("#messagediverrorjs>div").html("$0 Procurements  cannot be changed to have estimated contract values greater than $0 after being released.");
		return false;
	}
	return true;
}
/**
 *  Start of changes for release 3.2.0 enhancement 5684
 *  Function for launching PCOF task by city user
 *  Method updated for Release 3.3.0 #defect 6458 : to remove code for passing proc title in  request paramaters.
 *  R5 changes start
 **/
function launchPCOF(procurementId, launchPSR){
	   pageGreyOut();
	   var urlAppender = $("#launchPCOF").val() + "&ProcID="+procurementId + "&agencyId="+agencyID + "&OnlyPSR="+launchPSR;
	   jQuery.ajax({
	   type: "POST",
	   url: urlAppender,
	   success: function(result){
		   	removePageGreyOut();
		   	if(result["error"]==0){
			   	$("#successMessagePCOF").html(result["message"]);
				$("#successMessagePCOF").show();
				$("#generatePCoFTask").attr("disabled","disabled");
				// 8403 changes
				$('#generateTaskFlag').val(0);
				//8403 changes end
		   	}
		   	else if(result["error"]==2){
		   		$("#messagediverrorjs").html(result["message"]);
				$("#messagediverrorjs").show();
				$("#generatePCoFTask").attr("disabled","disabled");
		   	}
		   	else{
		   		$("#messagediverrorjs").html(result["message"]);
				$("#messagediverrorjs").show();
		   	}
 		 },
	   error : function(data, textStatus, errorThrown) {
  					showErrorMessagePopup();
						removePageGreyOut();
	   }
	 });
	// End of changes for release 3.2.0 enhancement 5684 
}


/**
 *  Added in R5, To Submit Procurement Form
 **/
function submitProcurementForm(generateTaskflag, regeneratePDFFlag){
	var procurementValue = $("#estProcurementValue").val();
	if(procurementValue != '') {
		$("#estProcurementValue").val(procurementValue.replaceAll(",",""));
	}
	if(regeneratePDFFlag != null && typeof regeneratePDFFlag !="undefined"){
		$("#regeneratePDFFlag").val(regeneratePDFFlag);
	} 
	if(generateTaskflag != null && typeof generateTaskflag !="undefined"){
		$("#generateTaskFlag").val(generateTaskflag);
	}
	//added as part of defect 5633, build 2.6.0
	$("#serviceUnitRequired").attr("disabled", false);
	document.addProcurementform.submit();
}
/**
 * This method is added to check Conditions On procurement Planned Status.
 **/
function checkConditionsOnPlannedStatus(){
	var currentEstAmount = $("#estProcurementValue").val();
	var currentContractStartDate = $("#contractStartDateUpdated").val();
	var currentContractEndDate = $("#contractEndDateUpdated").val();
	var procurementStatus = $("#procurementStatus").val();
	if(previousEstAmount == "0")
		previousEstAmount = "0.00";
	// Start:Changes for 8403
	if(procurementStatus == '2' && (new Big(currentEstAmount.replaceAll(",","")).minus(new Big(previousEstAmount.replaceAll(",",""))) !=0 ||
			previousContractStartDate != currentContractStartDate ||
			previousContractEndDate != currentContractEndDate) && taskLaunch == "true")
	// End	
	{
		launchConfirmOverlayPcofPSR("Confirm Changes", "<div>You have changed one or more of the following fields: <br />Estimated Procurement Value, Contract Start Date, Contract End Date.  <br /><p>&nbsp;</p>If you confirm this change, you may need to <br />regenerate the PCoF and/or PSR approval workflow before the procurement can be released</div>");
		return false;
	}
	else
	{
		return true;
	}
}
/**
 * This method is called to check if a new psr task is needed to be launched based on contract start date end date.
 * */
function launchConfirmOverlayPcofPSR(heading, message){
	var estProcurementValue = $("#estProcurementValue").val().replaceAll(",","");
	var contractStartDateUpdated = $("#contractStartDateUpdated").val();
	var contractEndDateUpdated = $("#contractEndDateUpdated").val();
	$.ajax({
		url : $("#verifyPcofPSR").val(),
		type : 'POST',
		cache : false,
		data: {procurementId:  $("#procurementId").val(), estProcurementValue: estProcurementValue, contractStartDateUpdated: contractStartDateUpdated, contractEndDateUpdated: contractEndDateUpdated},
		success : function(data) {
			if(data.approvePSRFlag && (data.Procurement.psrConctractStartDate != contractStartDateUpdated 
					|| data.Procurement.psrConctractEndDate != contractEndDateUpdated)
					&& estProcurementValue == '0.00' 
					&& data.Procurement.psrApprovedAmount == parseInt(estProcurementValue)){
				submitProcurementForm(null, '1');
			}else if((data.approvePCOFFlag || data.approvePSRFlag) && (data.Procurement.pcofConctractStartDate != contractStartDateUpdated 
					|| data.Procurement.pcofConctractEndDate != contractEndDateUpdated 
					|| data.Procurement.approvedAmount != estProcurementValue
					|| data.Procurement.psrConctractStartDate != contractStartDateUpdated 
							|| data.Procurement.psrConctractEndDate != contractEndDateUpdated 
							|| data.Procurement.psrApprovedAmount != estProcurementValue) && (estProcurementValue != '0.00' )){
				$('<div id="dialogBox"></div>').appendTo('body')
	               .html(message)
	               .dialog({
	                     modal: true, title: heading, zIndex: 10000, autoOpen: true,
	                     width: 'auto', modal: true, resizable: false, draggable:false,
	                     dialogClass: 'dialogButtons',
	                     buttons: {
	                           Confirm: function () {
	                        	   submitProcurementForm('1', null);
	                        	   $(this).remove();
	                           },
	                           Cancel: function () {
	                        	   $(this).remove();
	                           }
	                     },
	                     close: function (event, ui) {
	                           $(this).remove();
	                     }
	               });
				$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
			}else if(data.approvePCOFFlag && (data.Procurement.pcofConctractStartDate == contractStartDateUpdated 
					&& data.Procurement.pcofConctractEndDate == contractEndDateUpdated 
					&& data.Procurement.approvedAmount == estProcurementValue)){
				submitProcurementForm('0', null);
			}else{
				submitProcurementForm(null, null);
			}
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
			showErrorMessagePopup();
		}
	});
}

// R5 changes end