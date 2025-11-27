/**
 * This file contains the functionality that hill handle events on evaluation screen.
 */
suggestionVal = "";
var isvalid = false;
var nameValue="";
/**
 * This method called when page is getting loaded 
 *  Updated Method in R4
 */
$(document).ready(function(){
	
	// Start || Added as a part of release 3.6.0 for enhancement request 5905
	$("#internalEvaluatorNames").val("");
	$("#externalEvaluatorNames").val("");
	// End || Added as a part of release 3.6.0 for enhancement request 5905

	// on click of return to evaluation summary button
	$("#returnEvaluationSummary").click(function(){
		$("#navigationForm").find("#forAction").eq(0).val("propEval");
		$("#navigationForm").find("#ES").eq(0).val("0");
		$("#navigationForm").find("#topLevelFromRequest").eq(0).val(
				"ProposalsandEvaluations");
		pageGreyOut();
		document.navigationForm.submit();
	});
	
	// on changing competition pool from drop down
	$("#compPoolDropDown").change(function(){
		if($(this).val()!=-1 && $(this).val()!=$("#navigationForm").find("#evaluationPoolMappingId").eq(0).val()){
			$("#navigationForm").find("#forAction").eq(0).val("propEval");
			$("#navigationForm").find("#topLevelFromRequest").eq(0).val(
					"ProposalsandEvaluations");
			$("#navigationForm").find("#midLevelFromRequest").eq(0).val(
					"EvaluationSettings");
			$("#navigationForm").find("#ES").eq(0).val("0");
			$("#navigationForm").find("#evaluationPoolMappingId").eq(0)
					.val($(this).val());
			pageGreyOut();
			document.navigationForm.submit();
		}
	});
			$("#agencyId").change(function() {
				agency = $("#agencyId").val();
				$("#internalItem").val("");
				if (agency != '') {
					$("#internalItem").removeAttr("disabled");
					setAgencyIntoSession($(this).val());
				} else {
					$("#internalItem").attr("disabled","disabled");
				}
			});
			//type head search for internal user
			typeHeadSearchAgency($('#internalItem'), $("#getInternalEvaluatorsUrl").val(),"internalAdd",
					"typeHeadCallBackInternal", $("#errorMsgInternal"),"internal");

			//type head search for external users
			typeHeadSearchAgency($('#externalItem'), $("#getExternalEvaluatorsUrl").val()+"&agencyId="+$("#agencyID").val(),"externalAdd",
					"typeHeadCallBackExternal", $("#errorMsgExternal"),"external");

			$('#internalItem').keyup(function() {
				if($("#providerId").val()!= '' && ($(this).val().toUpperCase())==nameValue) {
					$('#internalAdd').removeAttr("disabled");
				}
				else{
					$('#internalAdd').attr("disabled","disabled");
				}
			});
			
			$('#externalItem').keyup(function() {
				if ($("#providerId").val()!= '' && $.trim($('#externalEvaluator').val())!='' && ($(this).val().toUpperCase())==nameValue) {
					$('#externalAdd').removeAttr("disabled");
				} else {
					$('#externalAdd').attr("disabled", "disabled");
				}
			});
			
			$('#externalEvaluator').keyup(function() {
				var inputValue = $('#externalItem').val();
				var isvalid = isAutoSuggestValidAgency(inputValue, suggestionVal);
				
				if ($("#providerId").val()!= '' && $.trim($(this).val())!='' && isvalid) {
					$('#externalAdd').removeAttr("disabled");
				} else {
					$('#externalAdd').attr("disabled", "disabled");
				}
			});
			
			$("#internalItems").change(function(){
				if($("#internalItems option:selected").size()>0){
					$("#removeInternal").removeAttr("disabled");
				}else{
					$("#removeInternal").attr("disabled", "disabled");
				}
			});
			$("#externalItems").change(function(){
				if($("#externalItems option:selected").size()>0){
					$("#removeExternal").removeAttr("disabled");
				}else{
					$("#removeExternal").attr("disabled", "disabled");
				}
			});
			// check the condition for save button if less then 3 users display erros message otherwise save the data in database
			$("#saveEvaluators").click(function(){
				var internalCount = $('#internalItems option').size();
				var externalCount = $('#externalItems option').size();
				var finalCount = internalCount + externalCount;
				if(finalCount<3){
					$("#errorMessage").show();
					return false;
				}else{
					var internalIds = '';
					var externalIds = '';
					$('#internalItems option').each(function(){
						internalIds += $(this).val() + ',';
					});
					$('#externalItems option').each(function(){
						externalIds += $(this).val() + ',';
					});
					internalIds = internalIds.length > 0 ? internalIds.substring(0, internalIds.length - 1): "";
					externalIds = externalIds.length > 0 ? externalIds.substring(0, externalIds.length - 1): "";
					$("#internalEvaluatorNames").val(internalIds);
					$("#externalEvaluatorNames").val(externalIds);
					$("#evaluatorCount").val(finalCount);
					pageGreyOut();
					document.evaluationSettingsform.submit();
				}
			});
			// modified the check to make agency drop down read only if review score task has been generated once.
			if($("#isEvaluationScoreSend").val() =='true' || $("#isReviewTaskPresent").val() =='true' || $("#cityUser").val()=='city_org'){
				$("#agencyId").attr("disabled","disabled");
			}
			// set the selected agency 
			$("#agencyId option").each(function(){
				if($(this).val()==$("#agencyID").val()){
					$(this).attr("selected","selected");
				}
			});
			
			$('#saveDocumentData').click(function(){
				$("#evaluationSettingsform").attr("action",$("#hiddenSaveDocumentDetailsUrl").val());
				document.evaluationSettingsform.submit();
			});
		});

/**
 * This method is used to set the agency id into the session because type head url 
 * is already generated on page load, we can not set parameter when agency drop down has been changed
 * @param agencyId agency id needs to be set
 */
function setAgencyIntoSession(agencyId) {
	pageGreyOut();
	if (agencyId != '') {
		var jqxhr = $.ajax({
			url : $("#setAgency").val()+"&agencyId="+agencyId,
			type : 'POST',
			cache : false,
			success : function(data) {
				removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
} 

/**
 * This method is used to check when internal evaluator type head execution completed whether user exists or not
 */
function typeHeadCallBackInternal() {
	var inputValue = $('#internalItem').val();
	isvalid = isAutoSuggestValidAgency(inputValue, suggestionVal);
	if (!isvalid && $('#internalItem').val() != '' && $('#internalItem').val().length > 3){
		$(".autocomplete").html("").hide();
		suggestionVal = "";
		$('#errorMsgInternal').html("! User does not exist in the selected Agency. Please enter an existing user.");
	} else if (isvalid || $('#internalItem').val().length <= 3) {
		$('#errorMsgInternal').html("");
	}
}
/**
 * This method is used to check when external evaluator type head execution completed whether user exists or not
 */
function typeHeadCallBackExternal() {
	var inputValue = $('#externalItem').val();
	isvalid = isAutoSuggestValidAgency(inputValue, suggestionVal);
	if (!isvalid && $('#externalItem').val() != '' && $('#externalItem').val().length > 3){
		$(".autocomplete").html("").hide();
		suggestionVal = "";
		$('#errorMsgExternal').html("! User does not exist in the agency. Please select an existing user.");
	} else if (isvalid || $('#externalItem').val().length <= 3) {
		$('#errorMsgExternal').html("");
	}
}
/**
 * This method is used to add the internal and external users to the selected agency
 * This method also check whether user is already selected or not
 * @param type defines internal or external
 * Updated Method in R4
 */
function addItem(type) {
	var exists = false;
	// internal user for already selected user
	if(type == 'internal'){
		$('#internalItems option').each(function(){
			var internalEvaluatorName = $(this).val().split('~')[1];
		    if (internalEvaluatorName == $("#providerId").val()) {
		        $('#errorMsgInternal').html("! User is already selected");
		        $('#internalAdd').attr("disabled","disabled");
		        exists = true;
		        return false;
		    }
		});
		
	}else{// external user for already selected user
		$('#externalItems option').each(function(i){
			var externalEvaluatorName = $(this).val().split('~')[0];
			// Modified a check on evaluator name to avoid duplicate names 
		    if (escape($.trim($('#externalEvaluator').val().toUpperCase())) == $.trim(externalEvaluatorName.toUpperCase())
		    		|| $.trim($('#externalEvaluator').val().toUpperCase()) == $.trim(externalEvaluatorName.toUpperCase())) {
		        $('#errorMsgExternal').html("! User is already selected");
		        $('#externalAdd').attr("disabled", "disabled");
		        exists = true;
		        return false;
		    }
		});
	}
	if(!exists){
		if (type == 'internal' && $('#internalItem').val() != '') {
			$('#internalItems').append(
					'<option value="' + $("#agencyId option:selected").val() + "~" + $("#providerId").val()+ '">'
							+ $.trim($('#internalItem').val()) + '</option>');
			$('#internalItem').val('');
			$("#internalAdd").attr("disabled","disabled");
			$("#providerId").val('');
			nameValue='';
		} else if ((type == 'external' && $('#externalItem').val() != '') && ($('#externalEvaluator').val() != '')) {
			var externalEvaluatorName = $.trim($("#externalEvaluator").val());
			$('#externalItems').append(
					'<option value=' + escape(externalEvaluatorName) + '~'
					+ $("#providerId").val()+">"
							+ $.trim($('#externalEvaluator').val()) + "(via "+ $.trim($('#externalItem').val())+")" + '</option>');
			$("#externalAdd").attr("disabled","disabled");
			$('#externalEvaluator').val('');
			$('#externalItem').val('');
			$("#providerId").val('');
			nameValue='';
		}
	}
	
}
/**
 * This method is used to remove all the selected user from the selected box
 * @param obj button object
 * @param type internal or external
 */
function removeItem(obj,type) {
	if (type == 'internal') {
		$("#internalItems option:selected").remove();
	} else {
		$("#externalItems option:selected").remove();
	}
	$(obj).attr("disabled", "disabled");
	var count = $('#'+type+'Items option').size();
	if(count==0){
		$(obj).attr("disabled", "disabled");
	}
}

//This function handles functionality when user types any character in Add
// Provider text box and executes type head search for Agency
function typeHeadSearchAgency(inputBoxObj, url, buttonIdToEnable, typeHeadCallBack, errorSpanObj,type) {
	// This will execute when user types any character in Add
	// Provider text box.
	if (inputBoxObj != null
			&& typeof (inputBoxObj) != "undefined" && inputBoxObj.size() > 0) {
		inputBoxObj.keyup(function(evt) {
			var keyCode = evt ? (evt.which ? evt.which : evt.keyCode)
					: event.keyCode;
			if (keyCode == 13) {
				evt.stopPropagation();
				return false;
			}
			var valueToSearch = inputBoxObj.val();
			
			if (buttonIdToEnable != null
					&& typeof (buttonIdToEnable) != "undefined") {
				isValid = isAutoSuggestValidAgency(valueToSearch, suggestionVal);
				
				if (isValid) {
					document.getElementById(buttonIdToEnable).disabled = false;
				} else {
					document.getElementById(buttonIdToEnable).disabled = true;
				}
			}
		});
		var onAutocompleteSelect = function(value, data) {
			errorSpanObj.html("");
			isValid = true;
			if($("#providerId").length>0)
				$("#providerId").val(data);
			
			nameValue = value.toUpperCase();
			if (buttonIdToEnable != null
					&& typeof (buttonIdToEnable) != "undefined") {
				if(type=="external"){
					if($("#providerId").val()!= '' && $.trim($('#externalEvaluator').val())!=''){
						document.getElementById(buttonIdToEnable).disabled = false;
					}else{
						document.getElementById(buttonIdToEnable).disabled = true;
					}
				}else{
					document.getElementById(buttonIdToEnable).disabled = false;
				}
			}
		};
		var options = {
			serviceUrl : url,
			width : 240,
			minChars : 3,
			maxHeight : 150,
			onSelect : onAutocompleteSelect,
			callBackMethod : typeHeadCallBack,
			clearCache : true,
			params : {
				epin : inputBoxObj.val()
			}
		};
		inputBoxObj.autocomplete(options);
	}
}

// This will execute during type head search for provider
function isAutoSuggestValidAgency(variableName, suggestionVal) {
	var uoValid = false;
	if (suggestionVal.length > 0) {
		for (i = 0; i < suggestionVal.length; i++) {
			var arrVal = suggestionVal[i].toUpperCase();
			if (arrVal.indexOf(variableName.toUpperCase()) > -1) {
				uoValid = true;
				break;
			}
		}
	}
	return uoValid;
}