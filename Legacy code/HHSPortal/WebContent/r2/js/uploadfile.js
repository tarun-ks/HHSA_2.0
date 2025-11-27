/**
 * 	======================================================
 *  This will execute when any file is selected to upload
 *  ======================================================
 **/
function displayDocName(filePath) {
	var fullPath = filePath.value;
	var fileNameIndex = fullPath.lastIndexOf("\\") + 1;
	var filename = fullPath.substr(fileNameIndex);
	var docName = document.getElementById("hidden");
	var ext = filename.lastIndexOf(".");
	filename = filename.substr(0, ext);
	if(filename.length >= 1){
	$(".alert-box").find("#docName").val(filename);
	$(".alert-box").find("#hidden").show();
	}
	else{
		$(".alert-box").find("#hidden").hide();	
	}

	$(".alert-box").find(".docnameError").hide();
}

/**
 *  This will get the Document type list for selected Document category
 **/
function getDocumentTypeList(category, userOrg) {
	pageGreyOut();
	var url = $("#contextPathSession").val() + "/GetContent.jsp?selectedInput="
			+ category + "&organizationId=" + userOrg;
	postTypeRequest(url);
	removePageGreyOut();
}

/**
 *  This will process the document category request and will get the Document
 *	type response through servlet call
 **/
function postTypeRequest(strURL) {
	var xmlHttp;
	if (window.XMLHttpRequest) {
		var xmlHttp = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlHttp.open('POST', strURL, true);
	xmlHttp.setRequestHeader('Content-Type',
			'application/x-www-form-urlencoded');
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.readyState == 4) {
			updatetypepage(xmlHttp.responseText);
		}
	};
	xmlHttp.send(strURL);
}

/**
 *  This will update the Document Type drop down fetched through servlet call
 *  */
function updatetypepage(str) {
	var n = str.split("|");
	var selectbox = document.getElementById("doctype");
	var i;
	for (i = selectbox.options.length - 1; i >= 0; i--) {
		selectbox.remove(i);
	}
	if (null != n) {
		var optn = document.createElement("OPTION");
		optn.text = "";
		optn.value = "";
		optn.setAttribute("title", "");
		selectbox.options.add(optn);
		for ( var i = 0; i < n.length - 1; i++) {
			var optn = document.createElement("OPTION");
			optn.text = n[i];
			optn.value = n[i];
			optn.setAttribute("title", n[i]);
			selectbox.options.add(optn);
		}
	}
}

/**
 *  This will execute when any option is selected from Sample Document Category
 *  drop down
 * */
function selectSampleCategory() {
	if ($('#sampledoccategory').val() == "") {
		$("#sampledoctype").attr("disabled", "disabled");
	} else {
		$("#sampledoctype").removeAttr("disabled");
	}
}

/**
 *  This will get the Document category and type list for selected Document
 *	category
 **/
function getSampleCategory() {
	var url = $("#contextPathSession").val()
			+ "/GetContent.jsp?&category=samplecategory";
	postRequestForSampleCategory(url);
}

/** 
 * This will process the above request and will get the Sample Document category
 * and type response through servlet call
 */
function postRequestForSampleCategory(strURL) {
	var xmlHttp;
	if (window.XMLHttpRequest) {
		var xmlHttp = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlHttp.open('POST', strURL, true);
	xmlHttp.setRequestHeader('Content-Type',
			'application/x-www-form-urlencoded');
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.readyState == 4) {
			updateSampleCategory(xmlHttp.responseText);
		}
	}
	xmlHttp.send(strURL);
}

/**
 *  This will update the Sample Document Category and Sample Document Type drop
 * down fetched through servlet call
 * */
function updateSampleCategory(str) {
	
	var catString = str.split("|");
	

	var catSelectbox = document.getElementById("sampledoccategory");
	

	for ( var i = catSelectbox.options.length - 1; i >= 0; i--) {
		catSelectbox.remove(i);
	}
	
	var optn = document.createElement("OPTION");
	optn.text = "";
	optn.value = "";
	optn.setAttribute("title", "");
	catSelectbox.options.add(optn);

	for ( var i = 0; i < catString.length; i++) {
		var optn = document.createElement("OPTION");
		optn.text = catString[i];
		optn.value = catString[i];
		optn.setAttribute("title", catString[i]);
		catSelectbox.options.add(optn);
	}
	
	document.getElementById("sampledoctype").disabled = true;
}

/**
 *  This will execute when Cancel button is clicked during file upload
 *  */
$(".alert-box").find('#cancel').unbind("click").click(function() {
	pageGreyOut();
	var docType = $("#uploadingDocumentType").val();
	if (docType == 'BAFO'){
		closeTaskOverlay();
	}
	//Start || Changes done for Enhancement #6429 for Release 3.4.0
	if (docType == 'awardDoc'){
		closeTaskOverlay();
	}
	//End || Changes done for Enhancement #6429 for Release 3.4.0
	// Start Updated in R5
	else if(docType != 'BAFO'){
	// End Updated in R5
	document.uploadform.action = $("#cancelUploadDocument").val();
	document.uploadform.submit();
	}
});

/**
 *  This will execute when Back button is clicked during file upload
 *  */
$(".alert-box")
		.find('#back1')
		.unbind("click")
		.click(
				function() {
					// Start Updated in R5
					pageGreyOut();
					displayuploadForm.action = $("#uploadback").val()
							+ '&submit_action=goBackAction' +"&hdncontractId="+$("#contractId").val()+"&uploadingDocumentType="+$("#uploadingDocumentType").val()+"&organizationId="+$("#organizationId").val();
					// End Updated in R5
					var options = {
						success : function(responseText, statusText, xhr) {
							var $response = $(responseText);
							var data = $response.contents().find(
									".overlaycontent");
							$("#tab1").empty();
							$("#tab2").empty();
							if (data != null || data != '') {
								$("#tab1").html(data.detach());
							}
							var overlayLaunchedTemp = overlayLaunched;
							var alertboxLaunchedTemp = alertboxLaunched;
							$("#overlayedJSPContent").html($response);
							overlayLaunched = overlayLaunchedTemp;
							alertboxLaunched = alertboxLaunchedTemp;
							callBackInWindow("onReady");
							$('ul')
									.removeClass(
											'ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
							// Start Updated in R5
							$('#step1').removeClass().addClass(
									'active').css('margin-left','0px');;
							$('#step2').removeClass().css({'margin-left':'0px','padding-left':'30px'});
							$('#step3').css('margin-left','0px');
							// End Updated in R5
							removePageGreyOut();

						},
						error : function(xhr, ajaxOptions, thrownError) {
							showErrorMessagePopup();
							removePageGreyOut();
						}
					};
					pageGreyOut();
					$(displayuploadForm).ajaxSubmit(options);
					return false;
				});

/**
 *  This will execute when Cancel button is clicked during file upload step 2
*	Updated Method in R4*/
// Start Updated in R5
$(".alert-box-upload").find('#cancel1,#cancel2').unbind("click").click(
						  // End Updated in R5
		function() {
			// Start Added in R5
			pageGreyOut();
			// End Added in R5
			var docType = $("#uploadingDocumentType").val();
			if (docType == 'BAFO'){
				closeTaskOverlay();
			}
			//Start || Changes done for Enhancement #6429 for Release 3.4.0
			if (docType == 'awardDoc'){
				closeTaskOverlay();
			}
			//End || Changes done for Enhancement #6429 for Release 3.4.0
			// Start Updated in R5
			else if(docType != 'BAFO'){
				pageGreyOut();
			$("#displayuploadForm").attr(
					"action",
					$("#uploadback").val()
							+ "&submit_action=cancelUploadDocument"
							+ "&procurementId=" + $("#procurementId").val());
			document.displayuploadForm.submit();
			// End Updated in R5
			}});

/**
 *  This will execute when any servlet call is made
 *  */
function postRequest(strURL) {
	var xmlHttp;
	if (window.XMLHttpRequest) {
		var xmlHttp = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlHttp.open('POST', strURL, true);
	xmlHttp.setRequestHeader('Content-Type',
			'application/x-www-form-urlencoded');
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.readyState == 4) {
			updatepage(xmlHttp.responseText);
		}
	};
	xmlHttp.send(strURL);
}

// This will execute to get filter document type for filter document category
function updatepage(str) {
	var n = str.split("|");
	var selectbox = document.getElementById("sampledoctype");
	var i;
	for (i = selectbox.options.length - 1; i >= 0; i--) {
		selectbox.remove(i);
	}
	if (null != n) {
		var optn = document.createElement("OPTION");
		optn.text = "";
		optn.value = "";
		optn.setAttribute("title", "");
		selectbox.options.add(optn);
		for ( var i = 0; i < n.length - 1; i++) {
			var optn = document.createElement("OPTION");
			optn.text = n[i];
			optn.value = n[i];
			optn.setAttribute("title", n[i]);
			selectbox.options.add(optn);
		}
	}
}

/**
 *  This will execute when Cancel button is clicked during file upload first
 *  scrren from task
 *  */
$(".alert-box").find('#cancelAwardDoc').unbind("click").click(function() {
	$(".overlay").closeOverlay();
});

/**
 * This will execute when Cancel button is clicked during file upload step 2*/
$("#cancelTaskDocument1").click(function() {
	// Start Added in R5
	$('#step1').removeClass().addClass('active').css({'margin':'0px'});;
	$('#step2').removeClass().css({'padding':'0 30px'});
	$('#step3').removeClass().addClass('last');
	// End Added in R5
	pageGreyOut();
	var url = $("#displayuploadForm").attr("action")+ "&submit_action=cancelUploadDocument"	+ "&procurementId=" + $("#procurementId").val();
	hhsAjaxRender(null, document.displayuploadForm,'attachDocuments, procPortlet',url,"closeTaskOverlay");
});

function closeTaskOverlay(){
	removePageGreyOut();
	$(".overlay").closeOverlay();
}

/**
 * Added for Release 5- typeAhead for docType
 * */	
var onAutocompleteSelect = function(value, data) {
		document.getElementById("doctype").value=value;
		document.getElementById("doccategory").value=data;
	    isValid = true;
};
var filterDocType = $("#filterDocumentType").val();
var url;
if(filterDocType == "RFP")
{
	url = $("#contextPathSession").val()+"/GetContent.jsp?&isFilter=true&requestingtype=rfp";
}else if(filterDocType == "agencyAwardDoc")
{
	url = $("#contextPathSession").val()+"/GetContent.jsp?&isFilter=true&requestingtype=agencyAwardDoc";
}
var options = {
 	serviceUrl: url,
   width: 260,
   minChars:3,
   maxHeight:100,
   onSelect: onAutocompleteSelect,
   clearCache: true,
   deferRequestBy: 0, //miliseconds
   params: { city: $("#doctype").val() }
};

/**
*  Added for Release 5
*  */
function isAutoSuggestValid(docType, suggestionVal) {
	var uoValid = false;
	if (suggestionVal.length > 0) {
		for (i = 0; i < suggestionVal.length; i++) {
			var arrVal = suggestionVal[i];
			if (arrVal == docType) {
				uoValid = true;
				break;
			}
		}
	}
	return uoValid;
}  

/* Release 5 ends */