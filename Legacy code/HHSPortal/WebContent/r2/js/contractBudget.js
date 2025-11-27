/**
 * =====================================================
 * This file handle the event on Contract Budget Screen.
 * =====================================================
 */	

/**
* This function called On click of save on Contract budget Screen.
**/
	function onSaveClick() {
		clearTopLevelMessage();
		$("#error").html("");
		$("#error").hide();
		if(validateComment()){
		pageGreyOut();
		var v_parameter = "&budgetID=" + budgetID + "&contractID=" + contractID  + "&publicCommentArea=" + convertSpecialChar($("#publicCommentArea").val());
		var urlAppender = $("#saveContractBudgetUrl").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(result) {
				if(result.indexOf("contractFYBudget")!=-1){
					$("#assignAdvanceId").html(result);
					$("#successGlobalMsg").show();
		        	$("#successGlobalMsg").html(successMessage);
		        	resetFlag();
		        }else {
		        	$("#errorGlobalMsg").html(result);
					$("#errorGlobalMsg").show();
		        }
				removePageGreyOut();
			},
			error : function(result) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
		}else{
			$("#errorGlobalMsg").html(invalidResponseMsg);
			$("#errorGlobalMsg").show();
		}
	}
/**
 * 	 This function is called to clear And Close OverLay.
 * */
	function clearAndCloseOverLay() {
		$("#overlayDivId").html("");
		$(".overlay").closeOverlay();
	}
/**
 * 	 function to refresh non grid data for contracted services
 * */
	function refreshNonGridContractedServicesData(subBudgetIdVal){
		var v_parameter = '&nextAction=getContractedServicesData&subBudgetId='+subBudgetIdVal;
		var urlAppender = $("#getCallBackContractBudgetData").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#totCS"+e['SubBudgetId']).html(e['ProposedTotalContractedServicesAmount']).jqGridCurrency();
				$("#ytdIA"+e['SubBudgetId']).html(e['TotalYtdInvoiceAmount']).jqGridCurrency();
				return false;
			},
			error : function(data, textStatus, errorThrown) {
				removePageGreyOut();
			}
		});
	}
	
/**
 * 	 function to refresh non grid data for personnel services 
 * */
	function refreshNonGridData(subBudgetIdVal){
		var v_parameter = '&nextAction=getPersonnelServicesData&subBudgetId='+subBudgetIdVal;
		var urlAppender = $("#getCallBackContractBudgetData").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#val1"+e['SubBudgetId']).html(e['TotalSalaryAndFringeAmount']).jqGridCurrency();
				$("#val2"+e['SubBudgetId']).html(e['TotalSalaryAmount']).jqGridCurrency();
				$("#val3"+e['SubBudgetId']).html(e['TotalFringeAmount']).jqGridCurrency();
				if (e['FringePercentage'] == 0) {
					$("#val5" + e['SubBudgetId']).html('(0.00%)');
				} else {
					if (e['FringePercentage'].indexOf('E-') !== -1 || e['FringePercentage'].indexOf('e-') !== -1) {
						$("#val5" + e['SubBudgetId']).html('('+ new Big(Math.round(e['FringePercentage'].replaceAll('e-', 0).replaceAll('E-', 0) * 100) / 100).toFixed(2)+ '%)');
					} else {
						$("#val5" + e['SubBudgetId']).html("(" +new Big(Math.round(e['FringePercentage'] * 100) / 100).toFixed(2)+ "%)");
					}
				}
				$("#val4"+e['SubBudgetId']).html(e['TotalYtdInvoicedAmount']).jqGridCurrency();
				return false;
			},
			error : function(data, textStatus, errorThrown) {
				removePageGreyOut();
			}
		});
	}	

	/**
	 *  Js function for contract budget review task jsp
	 *  */
	function trim(stringToTrim) {
			return stringToTrim.replace(/^\s+|\s+$/g,"");
	}

	/**
	 *  This method is called for client side finish task validation
	 *  */
function finishTaskValidation(){
		var returnVal = true;
		var publicCommentVal = "";
		var internalCommentVal = "";
		var awardEpinVal =trim($("#awardEpinSpan").html()); 
		if(document.getElementById("internalCommentArea")!=null){
			internalCommentVal=trim(document.getElementById("internalCommentArea").value);
		}if(document.getElementById("publicCommentArea")!=null){
			publicCommentVal=trim(document.getElementById("publicCommentArea").value);
		}
		
		var taskStatus = $("#finishtaskchild").val();
		if(taskLevel==1 && publicCommentVal=="" && taskStatus=="Returned for Revision"){
			$("#taskErrorDiv").html(publicCommentErrorMsg);
			$("#taskErrorDiv").show();
			returnVal = false;
		}else if(taskLevel>1 && internalCommentVal=="" && taskStatus=="Returned for Revision"){
			$("#taskErrorDiv").html(internalCommentErrorMsg);
			$("#taskErrorDiv").show();
			returnVal = false;
		}else if(awardEpinVal=="" && (taskStatus=="Approved" || taskStatus=="")){
			$("#taskErrorDiv").html(awardEpinNotAssignErrorMsg);
			$("#taskErrorDiv").show();
			returnVal = false;
		}
		return returnVal;
	}


/**
 *  This function executes on click of save button
 *  */
function openOverlay() {
	clearTopLevelMessage();
	pageGreyOut();
	var jspName = "submitCBConfirmation";
		//Release 3.6.0 Enhancement id 6484
	var v_parameter = "&jspName=" + jspName + "&budgetID=" + budgetID + "&fiscalYearID=" +fiscalYearID
			+ "&contractID=" + contractID + "&agencyID=" + "${contractInfo.agencyId}" + "&currentProcurementId="+$('#currentProcurementId').val();
	var urlAppender = $("#submitContractBudgetOverlay").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(result) {
			if (result["error"] == 1)// For validation failure
			{
				$("#errorGlobalMsg").show();
				$("#errorGlobalMsg").html(result["message"]);
			} 
			else if(result["error"] == 2) // Application exception
			{
				$("#errorGlobalMsg").show();
				$("#errorGlobalMsg").html(result["message"]);
			}else{
				$("#overlayDivId").html(result);
				$(".overlay").launchOverlayNoClose(
						$(".alert-box-submit-contract"), "850px", null,
						"onReady");
				$("a.exit-panel").click(function() {
					clearAndCloseOverLay();
				});
			}
		    removePageGreyOut();	
		}
	});
}

/**
 *  This function will clear top level success and error message
 *  */
function clearTopLevelMessage(){
	$("#errorGlobalMsg").html("");
	$("#errorGlobalMsg").hide();
	$("#successGlobalMsg").html("");
	$("#successGlobalMsg").hide();
}


/**
 * function to refresh non grid data for OTPS screen, contract budget module
 * */
function refreshNonGridDataContBudOTPS(subBudgetIdVal){
	var v_parameter = '&nextAction=getOperationSupportData&subBudgetId='+subBudgetIdVal;
	var urlAppender = $("#getCallBackContractBudgetData").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#fyBudgetOTPS"+e['SubBudgetId']).html(e['keyFYBudgetOTPS']).jqGridCurrency();
			$("#ytdInvAmtOTPS"+e['SubBudgetId']).html(e['keyYTDInvAmtOTPS']).jqGridCurrency();
			return false;
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
}

/**
 *  This method refreshes the indirect rate change
 *  */
function refreshNonGridIndirectRateData(subBudgetIdVal){
	var v_parameter = '&nextAction=getIndirectRateData&subBudgetId='+subBudgetIdVal;
	var urlAppender = $("#getCallBackContractBudgetData").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			//Updated in R7
			$("#indirectRate"+subBudgetIdVal).text(e.keyIndirectRatePercent);
			$("#indirectPIRate"+subBudgetIdVal).text(e.keyPIIndirectRatePercent);
			//R7 End
			return false;
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
}

/**
 * Added for Release 3.4.0, #5681
*This method invokes Printer Friendly View for Print Budget
**/
function PrintView() {
	var a_href = $("#printBudget").attr('href') + "&removeMenu=";
	window.open(a_href);
}

/**
 * Start : Added in R5 
*  Function : form submit on select of View Contract
*  */
function submitFormToViewContractList(contractId){
	var formId = $('form').attr('id');
	$("#"+formId).attr("action", $("#hiddenNavigateListScreenUrl").val()+"&listAction=contractListAction&contractId="+contractId);
	$("#"+formId).submit();
}
/**
 * Function : form submit on select of View Invoices
 * */
function submitFormToViewInvoiceList(contractId, budgetId, invoiceId){
	var formId = $('form').attr('id');
	var $contractId = "";
	if(contractId != null)
	{
		$contractId = "&contractId="+contractId;
	}
	var $budgetId = "";
	if(budgetId != null)
	{
		$budgetId = "&budgetId="+budgetId;
	}
	var $invoiceId = "";
	if(invoiceId != null)
	{
		$invoiceId = "&invoiceId="+invoiceId;
	}
	$("#"+formId).attr("action", $("#hiddenNavigateListScreenUrl").val()+"&listAction=invoiceListAction"+$contractId+$invoiceId+$budgetId);
	$("#"+formId).submit();
}
/**
 * Function : form submit on select of View Payment
 * */
function submitFormToViewPaymentList(contractId, budgetId){
	var formId = $('form').attr('id');
	var $budgetId = "";
	if(budgetId != null)
	{
		$budgetId = "&budgetId="+budgetId;
	}
	$("#"+formId).attr("action", $("#hiddenNavigateListScreenUrl").val()+"&listAction=paymentListAction&contractId="+contractId+$budgetId);
	$("#"+formId).submit();
}
/**
 * Function : form submit on select of View Budget
 * */
function submitFormToViewBudgetList(contractId, budgetId){
	var $budgetId = "";
	if(budgetId != null){
		$budgetId = "&budgetId="+budgetId;
	}
	var formId = $('form').attr('id');
	$("#"+formId).attr("action", $("#hiddenNavigateListScreenUrl").val()+"&listAction=budgetListAction&contractId="+contractId+"&budgetType=Contract Budget"+$budgetId);
	$("#"+formId).submit();
}
//End : Added in R5

//Start : Added in R6
/**
* This function called to refresh non-grid data on PS Enhancement Screen
* Added in Release 6
**/
function refreshPSSummaryNonGridData(subBudgetIdVal, PSScreen){
	pageGreyOut();
	var txnId = '';
	if(PSScreen == 'PSSummaryScreen')
		txnId = 'getPersonnelServicesSummaryData';
	else
		txnId = 'getPersonnelServicesDetailData';
	var v_parameter = '&nextAction='+ txnId + '&subBudgetId='+subBudgetIdVal;
	var urlAppender = $("#getCallBackContractBudgetData").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			var fringePercent = new Big(Math.round(e['FringePercentage'] * 100) / 100).toFixed(2) + '%';
			var fringeObj;
			if(PSScreen == 'PSSummaryScreen'){
				$("#val1"+e['SubBudgetId']).html(e['CitySalaryAndFringeAmount']).jqGridCurrency();
				$("#val2"+e['SubBudgetId']).html(e['CitySalaryAmount']).jqGridCurrency();
				$("#val3"+e['SubBudgetId']).html(e['CityFringeAmount']).jqGridCurrency();
				$("#val4"+e['SubBudgetId']).html(" ("+fringePercent+")");
				$("#val5"+e['SubBudgetId']).html(e['TotalYtdInvoicedAmount']).jqGridCurrency();
				$("#val6"+e['SubBudgetId']).html(e['Position']);
				fringeObj = $('#table_contractBudgetFringeSummary-'+e['SubBudgetId']+'_>tbody>tr:eq(1)>td:eq(1)');
			}
			else{
				$("#val8"+e['SubBudgetId']).html(e['DetailedScreenMessage']);
				$("#val1"+e['SubBudgetId']).html(e['CitySalaryAndFringeAmount']).jqGridCurrency();
				$("#val2"+e['SubBudgetId']).html(e['CitySalaryAmount']).jqGridCurrency();
				$("#val3"+e['SubBudgetId']).html(e['CityFringeAmount']).jqGridCurrency();
				$("#val4"+e['SubBudgetId']).html(" ("+fringePercent+")");
				$("#val6"+e['SubBudgetId']).html(e['Position']);
				$("#val7"+e['SubBudgetId']).html(new Big(Math.round(e['totalCityFte'] * 100) / 100).toFixed(2));
				fringeObj = $('#table_fringeBenifitsGrid-'+e['SubBudgetId']+'>tbody>tr:eq(1)>td:eq(2)');
				fringeObj.removeClass();
				fringeObj.css("text-align", "right");
			}
			fringeObj.html(fringePercent);
			fringeObj.attr('title',fringePercent);
			return false;
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
	// Start R 7.12.0
	 $('#table_fringeBenifitsGrid-'+subBudgetIdVal).trigger("reloadGrid"); 
    // End R 7.12.0
}

/**
* This function called to refresh the FringeGrid header(Rate Column)
* Added in Release 6
**/
function refreshFringGridHeader(subBudgetIdVal, tableObj, isBold){
	pageGreyOut();
	var cellValue = $('#val4'+subBudgetIdVal).html().replace('(','').replace(')','');
	var fringeObj = $('#'+tableObj);
	fringeObj.removeClass();
	var _style = "";
	if(isBold){
		_style = "font-weight:bold;";
		
	}
	fringeObj.attr('style',_style+'text-align:center');
	fringeObj.html(cellValue);
	fringeObj.attr('title',cellValue);
	removePageGreyOut();
}

/**
* This function called On loadcomplete of Hourly grid to calculate the percentage.
* Added in Release 6
**/
function refreshHourlyPositionHeader(subBudgetIdVal, jsonObj){
	pageGreyOut();
	var tableIdObj = '#table_hourlyPositionDetailsGrid-';
	var tbodyObj = '>tbody>tr:eq(1)>td:eq(';
	var fyBudgetHeaderObj = $(tableIdObj+subBudgetIdVal+tbodyObj+'6)');
	var cityFundedHeaderObj = $(tableIdObj+subBudgetIdVal+tbodyObj+'7)');
	var totalRateNHour = null;
	for(var i=0; i<jsonObj.rows.length; i++){
		if(totalRateNHour == null){
			totalRateNHour =  new Big(jsonObj.rows[i].rate).times(jsonObj.rows[i].hourPerYear);
		}
		else{
			totalRateNHour = new Big(totalRateNHour).plus(new Big(jsonObj.rows[i].rate).times(jsonObj.rows[i].hourPerYear));
		}
	}
	var cityFundVal = null;
	if(totalRateNHour != null && totalRateNHour != 0){
		cityFundVal = new Big(fyBudgetHeaderObj.html().replace('$','').replaceAll(',','')).div(new Big((totalRateNHour==null)?0:totalRateNHour)).times(100).toFixed(2) + '%';
		if(cityFundVal.indexOf('NaN%') === 0 || cityFundVal.indexOf('Infinity%') === 0){
			cityFundVal = "0.00%";
		}
	}
	else{
		cityFundVal = "0.00%";
	}
	cityFundedHeaderObj.html(cityFundVal);
	cityFundedHeaderObj.attr('title',cityFundVal);
	removePageGreyOut();
}

/**
* This function called On loadcomplete to remove ".00" for jqgrid(salaried and hourly) header
* Added in Release 6
**/
function formatPositionHeader(tableId){
	var objTbId = $('#'+tableId);
	var tbIdVal = objTbId.html().replace('.00',''); 
	objTbId.html(tbIdVal);
	objTbId.attr('title',tbIdVal);
}
//End : Added in R6