/**
 * ==========================================================
 * This js file handle the events on Contract Invoice Screen.
 * This file is updated in R7.
 * ==========================================================
 */

/**
 *  for clearing and closing the overlay
 * */
function clearAndCloseOverLay() {
	$("#overlayDivId").html("");
	$(".overlay").closeOverlay();
}

/**
 *  for clearing or error messages
 **/
function clearAllErrorMsgs() 
{
	$("#errorGlobalMsg").hide();
	$("#errorGlobalMsg").html("");
}

/**
 *  for checking the invoice status
 **/
function checkInvoiceStatus() {
	var invoiceStatus = $("#<StatusLabelId>").html();
	if (invoiceStatus == "Pending Submission" ||  invoiceStatus == "Returned for Revision") {
		openSubmitOverlay();
	}
	else{
		return false;
	}
}

/**
 *  for validating invoice review checks
 **/
function openOverlay()	{
	if(validateServiceDate()){
	$("#errorGlobalMsg").html("");
	$("#errorGlobalMsg").hide();
	$("#successGlobalMsg").html("");
	$("#successGlobalMsg").hide();
	if(validateComment()){
	   pageGreyOut();
		var budgetId = $('#budgetId').val();
		var contractId = $('#contractId').val();
		var invoiceId = $('#invoiceId').val();		
		
		var startDate = $('#invStartDate').val();	
		var endDate = $('#invEndDate').val();		
		
		var publicComment = " ";// $("#publicCommentArea").val();

		var v_parameter = "&contractId=" + contractId + "&invoiceId=" + invoiceId + "&budgetId=" + budgetId + "&startDate=" + startDate+ "&endDate=" + endDate;
		
		var urlAppender = $("#invoiceStatusValidationVar").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(result) {
				clearAllErrorMsgs();
				if (result["error"] == 1)// Status not validated
				{
					$("#errorGlobalMsg").show();
					$("#errorGlobalMsg").html(result["message"]);
					removePageGreyOut();
					return false;
				} 
				else if(result["error"] == 2) // Application exception
				{
					$("#errorGlobalMsg").show();
					$("#errorGlobalMsg").html(result["message"]);
					removePageGreyOut();
					return false;
				} else {
					formDataChange = false;
					openSubmitOverlay();
				}
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}else{
		$("#errorGlobalMsg").html(invalidResponseMsg);
		$("#errorGlobalMsg").show();
	}
	}
}

/**
 *  for opening the overlay
 **/
function openSubmitOverlay() {
	var budgetId = $('#budgetId').val();
	var contractId = $('#contractId').val();
	var invoiceId = $('#invoiceId').val();
	var publicComment = convertSpecialChar($("#publicCommentArea").val());

	var jspName = "invoiceSubmissionConfirmation";
	var v_parameter = "&jspName=" + jspName + "&budgetId=" + budgetId
			+ "&contractId=" + contractId + "&invoiceId=" + invoiceId
			+ "&publicCommentArea=" + publicComment + "&provider="
			+ $('#prvInvNum').val() + "&invoiceStartDate="
			+ $('#invStartDate').val() + "&invoiceEndDate="
			+ $('#invEndDate').val();
	
	var urlAppender = $("#invoiceSubmissionOverlayVar").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
		//fix done as a part of release 3.1.2 defect 6420 - start
			if(e==null || e==''){
				redirectTo = $("#contextPathSession").attr("value")+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=invoiceListAction&fromMultipleInvoice=true";
				location.href= redirectTo;
			}
			else{
			//fix done as a part of release 3.1.2 defect 6420 - end
			$("#overlayDivId").html(e);
			$(".overlay").launchOverlayNoClose($(".alert-box-submit-invoice"),
					"850px", null, "onReady");
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
			});
			removePageGreyOut();
			}
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 *  for opening the overlay for assignee
 *  */
function openOverlayAssignee() {
	pageGreyOut();
	var jspName = "addAssignments";
	var v_parameter = "&jspName=" + jspName + "&budgetId=" + $('#budgetId').val() ;
	var urlAppender = $("#addAssigneeOverlayVar").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayDivId1").html(e);
			$(".overlay").launchOverlayNoClose(
					$(".alert-box-add-assign"), "550px", null,
			"onReady");
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay1();
			});
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}

/**
 *  for clearing and closing the overlay1
 *  */
function clearAndCloseOverLay1() {
	$("#overlayDivId1").html("");
	$(".overlay").closeOverlay();
}

/**
 * Call on click of save button
 * */
function onSaveClick(invoiceId) {
	$("#errorGlobalMsg").html("");
	$("#errorGlobalMsg").hide();
	$("#successGlobalMsg").html("");
	$("#successGlobalMsg").hide();
	if (validateServiceDate()) {
	if(validateComment()){	
	pageGreyOut();
	var v_parameter = "&invoiceId=" + invoiceId + "&provider=" + $('#prvInvNum').val()
				+ "&invoiceStartDate=" + $('#invStartDate').val() + "&invoiceEndDate="
				+ $('#invEndDate').val()+"&publicCommentArea="+convertSpecialChar($("#publicCommentArea").val());
		var urlAppender = $("#saveContractInvoiceUrl").val();
		pageGreyOut();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(result) {
			//fix done as a part of release 3.1.2 defect 6420 - start
				if(result==null || result==''){
					redirectTo = $("#contextPathSession").attr("value")+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=invoiceListAction&fromMultipleInvoice=true";
					location.href= redirectTo;
				}
				else{
				//fix done as a part of release 3.1.2 defect 6420 - end
				  if(result.indexOf("assignAdvanceTable")!=-1){
		        	$("#assignAdvanceId").html(result);
		        	resetFlag();
		        }else {
		        	$("#errorGlobalMsg").html(result);
					$("#errorGlobalMsg").show();
		        }
		    	removePageGreyOut();
				}
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
}


/**
 * This function is used to trim the string passed as input
 **/
function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

/**
 * This method is called for client side finish task validation
 * */
function finishTaskValidation(){
		var returnVal = true;
		var publicCommentVal = "";
		var internalCommentVal = "";
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
			$("#taskErrorDiv").html(internalAgencyCommentErrorMsg);
			$("#taskErrorDiv").show();
			returnVal = false;
		}
		return returnVal;
	}


/**
* Call on click of save button to save the Agency invoice number and refresh
*  the top information of the page
*  */
function onReviewSaveClick(invoiceId) {
	if(validateComment()){
	pageGreyOut();
	var agencyInvoiceNumber = $("#invoiceNumber").val();

	var v_parameter = "invoiceId=" + invoiceId+"&invoiceNumber=" + agencyInvoiceNumber+"&publicCommentArea="+convertSpecialChar($("#publicCommentArea").val())+"&internalCommentArea="+convertSpecialChar($("#internalCommentArea").val());
	var urlAppender = $("#hdnSaveContractInvoice").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(result) {
	        if(result.indexOf("assignAdvanceTable")!=-1){
	        	$("#assignAdvanceId").html(result);
	        	formDataChange = false;
	        }else {
	        	$("#taskErrorDiv").html(result);
				$("#taskErrorDiv").show();
	        }
	    	removePageGreyOut();
		}
	});
	}else{
		$("#errorGlobalMsg").html(invalidResponseMsg);
		$("#errorGlobalMsg").show();
	}
}

/** 
 * validate date values
 * */
function validateServiceDate() {
	
	/* QC 9714 */	
	
	var startDate = $("#invStartDate").val();
	var endDate = $("#invEndDate").val();
	
	if(!isFourDigitYear(startDate)){		
		$("#errorGlobalMsg").html(startDate + ' - ' + serviceDateFromInvalid );		
		$("#errorGlobalMsg").show();
		document.getElementById("invStartDate").style.color = "red";
		return false;
	}else {
		document.getElementById("invStartDate").style.color = "black";
	}
	
	if(!isFourDigitYear(endDate)){		
		$("#errorGlobalMsg").html(endDate + ' - ' + serviceDateToInvalid);		
		$("#errorGlobalMsg").show();
		document.getElementById("invEndDate").style.color = "red";
		return false;
	}else {
		document.getElementById("invEndDate").style.color = "black";
	}
	/* QC 9714 end */
	
	if (!dates.inRange($("#invStartDate").val(), $("#fiscalStartDate").html(), $("#fiscalEndDate").html())) {
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(serviceDateFrmNotInRange);
		/* QC 9714 */
		document.getElementById("invStartDate").style.color = "red";
		return false;
	}else{
		$("#errorGlobalMsg").hide();
		/* QC 9714 */
		document.getElementById("invStartDate").style.color = "black";
	}
	if (!dates.inRange($("#invEndDate").val(), $("#fiscalStartDate").html(), $("#fiscalEndDate").html())) {
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(serviceDateToNotInRange);
		/* QC 9714 */
		document.getElementById("invEndDate").style.color = "red";
		return false;
	}else{
		$("#errorGlobalMsg").hide();
		/* QC 9714 */
		document.getElementById("invEndDate").style.color = "black";
	}
	if (dates.compare($("#invEndDate").val(), $("#invStartDate").val()) == -1 || dates.compare($("#invEndDate").val(), $("#invStartDate").val()) == 0) {
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(serviceDateFromBeforeDateTo);
		/* QC 9714 */
		document.getElementById("invStartDate").style.color = "red";
		document.getElementById("invEndDate").style.color = "red";	
		/* QC 9714 end */
		return false;
	}else{
		$("#errorGlobalMsg").hide();
		/* QC 9714 */
		document.getElementById("invStartDate").style.color = "black";
		document.getElementById("invEndDate").style.color = "black";	
		/* QC 9714 end */
		return true;
	}
}

var dates = {
	    convert:function(d) {
	        // Converts the date in d to a date-object. The input can be:
	        //   a date object: returned without modification
	        //  an array      : Interpreted as [year,month,day]. NOTE: month is 0-11.
	        //   a number     : Interpreted as number of milliseconds
	        //                  since 1 Jan 1970 (a timestamp) 
	        //   a string     : Any format supported by the javascript engine, like
	        //                  "YYYY/MM/DD", "MM/DD/YYYY", "Jan 31 2009" etc.
	        //  an object     : Interpreted as an object with year, month and date
	        //                  attributes.  **NOTE** month is 0-11.	    	
	    	
	        return (
	            d.constructor === Date ? d :
	            d.constructor === Array ? new Date(d[0],d[1],d[2]) :
	            d.constructor === Number ? new Date(d) :
	            d.constructor === String ? new Date(d) :
	            typeof d === "object" ? new Date(d.year,d.month,d.date) :
	            NaN
	        );
	    },
	    compare:function(a,b) {
	        // Compare two dates (could be of any type supported by the convert
	        // function above) and returns:
	        //  -1 : if a < b
	        //   0 : if a = b
	        //   1 : if a > b
	        // NaN : if a or b is an illegal date
	        // NOTE: The code inside isFinite does an assignment (=).
	        return (
	            isFinite(a=this.convert(a).valueOf()) &&
	            isFinite(b=this.convert(b).valueOf()) ?
	            (a>b)-(a<b) :
	            NaN
	        );
	    },
	    inRange:function(d,start,end) {
	        // Checks if date in d is between dates in start and end.
	        // Returns a boolean or NaN:
	        //    true  : if d is between start and end (inclusive)
	        //    false : if d is before start or after end
	        //    NaN   : if one or more of the dates is illegal.
	        // NOTE: The code inside isFinite does an assignment (=).
	       return (
	            isFinite(d=this.convert(d).valueOf()) &&
	            isFinite(start=this.convert(start).valueOf()) &&
	            isFinite(end=this.convert(end).valueOf()) ?
	            start <= d && d <= end :
	            NaN
	        );
	    }
}

/* QC9714, 2023-05-16 */
function isFourDigitYear(yr){
	
	console.log('in isFourDigitYear with date -> ' + yr);
	
	if(!yr){
		return false;
	}
	
	if(yr == ''){
		return false;
	}
	
	const myArray = yr.split("/");
	if(myArray.length < 3){
		return false;
	}
	
	if(parseFloat(myArray[2]) < 1800){
		return false;
	}
	
	return true;
	
}

/* end of QC9714 */

/**
 * function to refresh non grid data for OTPS screen, contract budget module
 * */
function refreshNonGridDataInvoiceOTPS(subBudgetIdVal){
	var v_parameter = '&nextAction=getOperationSupportData&subBudgetId='+subBudgetIdVal;
	var urlAppender = $("#getCallBackContractInvoiceData").val();
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
 * function to refresh non grid data for contracted services
 * */
function refreshNonGridContractedServicesData(subBudgetIdVal){
	var v_parameter = '&nextAction=getContractedServicesData&subBudgetId='+subBudgetIdVal;
	var urlAppender = $("#getCallBackContractInvoiceData").val();
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
 * function to refresh non grid data for Personnel Service screen, contract budget Invoice module
 * */
function refreshNonGridData(subBudgetIdVal){
	var v_parameter = '&nextAction=getPersonnelServicesData&subBudgetId='+subBudgetIdVal;
	var urlAppender = $("#getCallBackContractInvoiceData").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#val1"+e['SubBudgetId']).html(e['TotalSalaryAndFringeAmount']).jqGridCurrency();
			$("#val2"+e['SubBudgetId']).html(e['TotalSalaryAmount']).jqGridCurrency();
			$("#val3"+e['SubBudgetId']).html(e['TotalFringeAmount']).jqGridCurrency();
			if(e['FringePercentage'] == 0){
				$("#val5"+e['SubBudgetId']).html('(0.00%)');
			}else{
				if(e['FringePercentage'].indexOf('E-') !== -1 || e['FringePercentage'].indexOf('e-') !== -1){
					$("#val5" + e['SubBudgetId']).html('('+ new Big(Math.round(e['FringePercentage'].replaceAll('e-', 0).replaceAll('E-', 0) * 100) / 100).toFixed(2)+ '%)');
				}else{
					$("#val5" + e['SubBudgetId']).html("(" +new Big(Math.round(e['FringePercentage'] * 100) / 100).toFixed(2)+ "%)");
				}
			}
			/* Start: added for 8454,8505 */
			if($('#existingBudget').val() == 0){
				refreshFringGridHeader(subBudgetIdVal);
			}
			/* End: added for 8454 */
			$("#val4"+e['SubBudgetId']).html(e['TotalYtdInvoicedAmount']).jqGridCurrency();
			return false;
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
}

/**
 * Added for Release 3.4.0, #5681
*  This method invokes Printer Friendly View for Print Budget
*  */
function PrintView() {
	var a_href = $("#printInvoice").attr('href') + "&removeMenu=";
	window.open(a_href);
}

/**
 * Start : Added in R5 
*  Function : form submit on select of View Contract
*  */
function submitFormToViewContractList(contractId){
	document.contractBudgetForm.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=contractListAction&contractId="+contractId;
	document.contractBudgetForm.submit();
}
/**
 * Function : form submit on select of View Budget
 * */
function submitFormToViewBudgetList(contractId, budgetId){
	document.contractBudgetForm.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=budgetListAction&contractId="+contractId+"&budgetType=Contract Budget&budgetId="+budgetId;
	document.contractBudgetForm.submit();
}

/**
 * Function : form submit on select of View Payment
 * */
function submitFormToViewPaymentList(contractId, invoiceId){
	document.contractBudgetForm.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=paymentListAction&contractId="+contractId+"&invoiceId="+invoiceId;
	document.contractBudgetForm.submit();
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
	var urlAppender = '';
	if(PSScreen == 'PSSummaryScreen'){
		txnId = 'getPersonnelServicesData';
	    urlAppender = $("#getCallBackContractInvoiceData").val();
	}else{
		txnId = 'getPersonnelServicesDetailData';
		urlAppender = $("#getCallBackContractBudgetData").val();
	}	
	var v_parameter = '&nextAction='+ txnId + '&subBudgetId='+subBudgetIdVal;
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			if(PSScreen == 'PSSummaryScreen'){
				var percentVal = parseFloat(e['FringePercentage']).toFixed(2)+'%';
				$("#val1"+e['SubBudgetId']).html(e['TotalSalaryAndFringeAmount']).jqGridCurrency();
				$("#val2"+e['SubBudgetId']).html(e['TotalSalaryAmount']).jqGridCurrency();
				$("#val3"+e['SubBudgetId']).html(e['TotalFringeAmount']).jqGridCurrency();
				$("#val5"+e['SubBudgetId']).html(" ("+percentVal+")");
				$("#val4"+e['SubBudgetId']).html(e['TotalYtdInvoicedAmount']).jqGridCurrency();
				$("#val6"+e['SubBudgetId']).html(e['Position']);
				var fringeObj = $('#table_fringeBenefitsGridInvoiceGrid-'+e['SubBudgetId']+'_>tbody>tr:eq(1)>td:eq(1)');
				fringeObj.html(percentVal);
				fringeObj.attr('title',percentVal);
			}
			else{
				$("#val8"+e['SubBudgetId']).html(e['DetailedScreenMessage']);
				$("#val1"+e['SubBudgetId']).html(e['CitySalaryAndFringeAmount']).jqGridCurrency();
				$("#val2"+e['SubBudgetId']).html(e['CitySalaryAmount']).jqGridCurrency();
				$("#val3"+e['SubBudgetId']).html(e['CityFringeAmount']).jqGridCurrency();
				$("#val4"+e['SubBudgetId']).html(" ("+e['FringePercentage']+"%)");
				$("#val6"+e['SubBudgetId']).html(e['Position']);
				$("#val7"+e['SubBudgetId']).html(e['totalCityFte']);
				var fringeObj = $('#table_fringeBenifitsGrid-'+e['SubBudgetId']+'>tbody>tr:eq(1)>td:eq(2)');
				fringeObj.removeClass();
				fringeObj.css("text-align", "right");
				fringeObj.html(e['FringePercentage']+"%");
				fringeObj.attr('title',e['FringePercentage']+"%");
			}
			removePageGreyOut();
			return false;
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
}

/**
* This function called On loadcomplete of Hourly grid to calculate the percentage.
* Added in Release 6
**/
function refreshHourlyPositionHeader(subBudgetIdVal, jsonObj){
	pageGreyOut();
	var tableIdObj = '#table_hourlyPositionDetailsGrid-';
	var tbodyObj = '>tbody>tr:eq(1)>td:eq(';
	var fyBudgetHeaderObj = $(tableIdObj+subBudgetIdVal+tbodyObj+'5)');
	var cityFundedHeaderObj = $(tableIdObj+subBudgetIdVal+tbodyObj+'6)');
	var totalRateNHour = null;
	for(var i=0; i<jsonObj.rows.length; i++){
		if(totalRateNHour == null){
			totalRateNHour = jsonObj.rows[i].rate * jsonObj.rows[i].hourPerYear;
		}
		else{
			totalRateNHour = totalRateNHour + jsonObj.rows[i].rate * jsonObj.rows[i].hourPerYear;
		}
	}
	var cityFundVal = ((fyBudgetHeaderObj.html().replace('$','').replace(',','')/totalRateNHour)*100).toFixed(2) + '%';
	if(cityFundVal.indexOf('NaN%') === 0){
		cityFundVal = "0.00%";
	}
	cityFundedHeaderObj.html(cityFundVal);
	cityFundedHeaderObj.attr('title',cityFundVal);
	removePageGreyOut();
}

/**
* This function called to refresh the FringeGrid header(Rate Column) on edit the fringe grid
* Added in Release 6
**/
function refreshFringGridHeader(subBudgetIdVal){
	pageGreyOut();
	var cellValue = $('#val5'+subBudgetIdVal).html().replace('(','').replace(')','');
	var fringeObj = $('#table_fringeBenefitsGridInvoiceGrid-'+subBudgetIdVal+'_>tbody>tr:eq(1)>td:eq(1)');
	fringeObj.html(cellValue);
	fringeObj.attr('title',cellValue);
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

/**
 * Added in R7
 * Function : This method will redirect from invoice screen to modified list screen.
 * */
function submitFormToModificationBudgetDetails(contractId, budgetId){
	document.contractBudgetForm.action = $("#hiddenNavigateListScreenUrl").val()+"&listAction=modificationBudgetRedirectionURL&contractId="+contractId+"&budgetType=Budget Modification&parentBudgetId="+budgetId;
	document.contractBudgetForm.submit();
}
//End R7
