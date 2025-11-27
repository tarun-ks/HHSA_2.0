// for clearing and closing the overlay
function clearAndCloseOverLay() {
	$("#overlayDivId").html("");
	$(".overlay").closeOverlay();
}

// for clearing or error messages
function clearAllErrorMsgs() 
{
	$("#errorGlobalMsg").hide();
	$("#errorGlobalMsg").html("");
}

// for checking the invoice status
function checkInvoiceStatus() {
	var invoiceStatus = $("#<StatusLabelId>").html();
	if (invoiceStatus == "Pending Submission" ||  invoiceStatus == "Returned for Revision") {
		openSubmitOverlay();
	}
	else{
		return false;
	}
}

// for validating invoice review checks
function openOverlay()	{
	$("#errorGlobalMsg").html("");
	$("#errorGlobalMsg").hide();
	$("#successGlobalMsg").html("");
	$("#successGlobalMsg").hide();
	   pageGreyOut();
		var budgetId = $('#budgetId').val();
		var contractId = $('#contractId').val();
		var invoiceId = $('#invoiceId').val();
		var publicComment = " ";

		var v_parameter = "&contractId=" + contractId + "&invoiceId=" + invoiceId + "&budgetId=" + budgetId;
		
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
					openSubmitOverlay();
				}
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	
}

// for opening the overlay
function openSubmitOverlay() {
	//pageGreyOut();
	var budgetId = $('#budgetId').val();
	var contractId = $('#contractId').val();
	var invoiceId = $('#invoiceId').val();
	var publicComment = " ";

	var jspName = "invoiceSubmissionConfirmation";
	var v_parameter = "&jspName=" + jspName + "&budgetId=" + budgetId + "&contractId=" 
	+ contractId + "&invoiceId=" + invoiceId + "&publicCommentArea=" + publicComment;
	var urlAppender = $("#invoiceSubmissionOverlayVar").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayDivId").html(e);
			$(".overlay").launchOverlayNoClose(
					$(".alert-box-submit-invoice"), "550px", null,
			"onReady");
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

// for opening the overlay for assignee
function openOverlayAssignee() {
	pageGreyOut();
	var jspName = "addPaymentAssignments";
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

// for clearing and closing the overlay1
function clearAndCloseOverLay1() {
	$("#overlayDivId1").html("");
	$(".overlay").closeOverlay();
}




//Call on click of save button
function onSaveClick(invoiceId) {
	$("#errorGlobalMsg").html("");
	$("#errorGlobalMsg").hide();
	$("#successGlobalMsg").html("");
	$("#successGlobalMsg").hide();
	if (validateServiceDate()) {
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
		        if(result.indexOf("assignAdvanceTable")!=-1){
		        	$("#assignAdvanceId").html(result);
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
	}
}


//THis function is used to trim the string passed as input
function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}
//This method is called for client side finish task validation
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
			$("#taskErrorDiv").html(internalCommentErrorMsg);
			$("#taskErrorDiv").show();
			returnVal = false;
		}
		return returnVal;
	}


//Call on click of save button to save the Agency invoice number and refresh
//the top information of the page
function onReviewSaveClick(invoiceId) {
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
}

//validate date values
function validateServiceDate()
{
	if (!dates.inRange($("#invStartDate").val(), $("#fiscalStartDate").html(), $("#fiscalEndDate").html())) 
	{
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(serviceDateFrmNotInRange);
		return false;
	}else{
		$("#errorGlobalMsg").hide();
	}
	if (!dates.inRange($("#invEndDate").val(), $("#fiscalStartDate").html(), $("#fiscalEndDate").html())) 
	{
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(serviceDateToNotInRange);
		return false;
	}else{
		$("#errorGlobalMsg").hide();
	}
	if (dates.compare($("#invEndDate").val(), $("#invStartDate").val()) == -1 ||
			dates.compare($("#invEndDate").val(), $("#invStartDate").val()) == 0) 
	{
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html(serviceDateFromBeforeDateTo);
		return false;
	}else{
		$("#errorGlobalMsg").hide();
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