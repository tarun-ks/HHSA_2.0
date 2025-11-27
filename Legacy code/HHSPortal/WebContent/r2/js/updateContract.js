//This js file is used for the page updateContract.jsp

var suggestionVal = "";
var isValid = false;

//This will add the rule on contract title fields which will validate for special characters
$.validator.addMethod("contractTitleValidation", function( value, element, param ) {
    return this.optional(element) 
        || (validateTextArea("contractTitlePopUp"));
});

$(document)
.ready(
		function() {
			$("#agencyId").prop('disabled', true);
			$("#contractValue").prop('disabled', true);
			$("#contractStartDate").prop('disabled', true);
			$("#contractEndDate").prop('disabled', true);
			putOnloadValidation();
			$('#contractValue').autoNumeric({
				vMax : '9999999999999999.99'
			});
			$("#updateContractButton").click(function(){	
				$("#ErrorDiv").hide();
				$("#transactionStatusDiv").hide();
			$("#updateContractForm").validate({
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
		            	//added check for start budget year, build 3.1.0, enhancement id 6020
                       	submitUpdateContract('');
 					},errorPlacement: function(error, element) {
				         	error.appendTo(element.parent().parent().find("span.error"));
				  	  }
	            });
			 });
	});


//This method is changed as part of build 3.1.0, enhancement ID 6020
//This method executes on click of Add Contract button
function submitUpdateContract(passedValue){
		pageGreyOut();
        //Build 3.1.0, enhancement 6020
		var accProgramNameList = document.getElementById("accProgramName");
		var newProgramId = accProgramNameList.options[accProgramNameList.options.selectedIndex].value;
		var newProgramName = accProgramNameList.options[accProgramNameList.options.selectedIndex].title;
		var v_parameter = "&" + $("#updateContractForm").serialize()+ "&programId=" + newProgramId+ "&programName=" + newProgramName;
		var urlAppender = $("#updateContractSubmitUrl").val();
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
	
