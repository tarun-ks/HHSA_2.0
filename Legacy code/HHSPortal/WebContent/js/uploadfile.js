/**
 * This will execute when any file is selected to upload
 * */
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
 * This will get the Document type list for selected Document category
 * */
function getDocumentTypeList(category, userOrg) {
	pageGreyOut();
	var url = $("#contextPathSession").val()+"/GetContent.jsp?selectedInput=" + category
			+ "&organizationId=" + userOrg;
	postTypeRequest(url);
	removePageGreyOut();
}

/**
 * This will process the document category request and will get the Document type response through servlet call
 * */
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
 * This will update the Document Type drop down fetched through servlet call
 * */
function updatetypepage(str) {
	var n = str.split("|");
	var selectbox = document.getElementById("doctype");
	var i;
	for (i = selectbox.options.length - 1; i >= 0; i--) {
		selectbox.remove(i);
	}
	if (null != n) {
		var optn = document.createElement("OPTION");
		selectbox.options.add(optn);
		for ( var i = 0; i < n.length - 1; i++) {
			var optn = document.createElement("OPTION");
			if(n[i] != 'Best and Final Offer (BAFO)'){
			optn.text = n[i];
			optn.value = n[i];
			optn.setAttribute("title", n[i]);
			selectbox.options.add(optn);
			}
		}
	}
}

/** 
 * This will execute when any option is selected from Sample Document Category drop down
 * */
function selectSampleCategory() {
	if($('#sampledoccategory').val() == ""){
		$("#sampledoctype").attr("disabled", "disabled");
	}
	else{
		$("#sampledoctype").removeAttr("disabled");
	}
}

/**
 * This will get the Document category and type list for selected Document category
 * */
function getSampleCategory() {
	var url = $("#contextPathSession").val()+"/GetContent.jsp?&category=samplecategory";
	postRequestForSampleCategory(url);
}

/**
 * This will process the above request and will get the Sample Document category and type response through servlet call
 * */
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
 * This will update the Sample Document Category and Sample Document Type drop down fetched through servlet call
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

// This will execute when Cancel button is clicked during file upload
$(".alert-box").find('#cancel').unbind("click").click(function() {
	pageGreyOut();
	document.uploadform.action = uploadfileForm+'&next_action=showdocumentlist&message=null&messageType=null&filtertrue=false';
	document.uploadform.submit();
	
	
});

// This will execute when Cancel button is clicked while uploading new version for document
$(".alert-box").find('#cancelnewversion').click(function() {
	$(".overlay").closeOverlay();
	return false;
});

/**
 * This will execute when Back button is clicked during file upload
 * */
$(".alert-box").find('#back1').unbind("click").click(function() { 
	
	displayForm.action=formAction+'&next_action=backrequest';
	var options = 
    			{	
				   	success: function(responseText, statusText, xhr ) 
					{
						var $response=$(responseText);
                            var data = $response.contents().find(".overlaycontent");
                            	$("#tab1").empty();
					 			$("#tab2").empty();
                            if(data != null || data != ''){
                                  $("#tab1").html(data.detach());
							}
						var overlayLaunchedTemp = overlayLaunched;
						var alertboxLaunchedTemp = alertboxLaunched;
						$("#overlayedJSPContent").html($response);
						overlayLaunched = overlayLaunchedTemp;
						alertboxLaunched = alertboxLaunchedTemp;
						callBackInWindow("onReady");
						
						$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
						$('#step1').removeClass('default').addClass('active');
						$('#step2').removeClass('activeLast active').addClass('last');	
						removePageGreyOut();

					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				};
				pageGreyOut();
				$(displayForm).ajaxSubmit(options);
				return false;
});

/**
 * This will execute when Cancel button is clicked during file upload step 2
 * */
$(".alert-box").find('#cancel1').unbind("click").click(function() {
	displayForm.action=formAction+'&next_action=cancelrequest';
	var options = 
		{	
		   	success: function(responseText, statusText, xhr ) 
			{
				$(".overlay").closeOverlay();
				$("#tab1").html("");
				$("#tab2").html("");
				removePageGreyOut();
			},
			error:function (xhr, ajaxOptions, thrownError)
			{                     
				showErrorMessagePopup();
				removePageGreyOut();
			}
		};
		pageGreyOut();
		$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
		$('#step1').removeClass('default').addClass('active');
		$('#step2').removeClass('activeLast active').addClass('last');
		$(displayForm).ajaxSubmit(options);
		return false;
});

//Method to close overlay(Copied from Document Valut-utils.js- to reset tabs classes)
$.fn.closeOverlay = function(e) {
	if(overlayLaunched != null){
		alertboxLaunched.hide();
		overlayLaunched.hide();
		alertboxLaunched = null;
		overlayLaunched = null;
		$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
		$('#step1').removeClass('default').addClass('active');
		$('#step2').removeClass('activeLast active').addClass('last');
	}
	callBackInWindow("closePopUp");
};


/**
 * This will execute when any servlet call is made
 * */
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

/**
 * This will execute to get filter document type for filter document category
 * */
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