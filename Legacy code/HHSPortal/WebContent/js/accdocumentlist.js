/* This will execute when Filter Documents tab is clicked or closed
Updated Method in R4*/
//changes done for defect 6253
function setVisibility(id, visibility) {
	//document.getElementById(id).style.display = visibility;
	callBackInWindow("closePopUp");
	
	if ($("#" + id).is(":visible") && $("#filterStatus").val() != 'filtered') {
		clearFilter(null,'null',$("#providerSet").val(),$("#agencySet").val());
		//document.forms[0].reset();
	}
	$("#" + id).toggle();
}

/* This will be executed when any option is selected from the action drop down
Updated Method in R4*/
function openDocument(documentId, selectElement, documentName){
		var value = selectElement.selectedIndex;
		if(value == 1){
			viewDocument(documentId, documentName);
			selectElement.selectedIndex = "";
		}
		else if(value == 2){
			pageGreyOut();
			$("#next_action").val('viewDocumentInfo');
			document.acceleratorForm.action = originalFormAction+'&removeNavigator=true&documentId='+documentId;
			document.acceleratorForm.submit();
		}
		else if(value == 3){
			pageGreyOut();
			selectElement.selectedIndex = "";
			$("#next_action").val('checkLinkDocumentBeforedelete');
			document.acceleratorForm.action = originalFormAction+'&removeNavigator=true&documentId='+documentId;
			// On click of 'delete document' option from action dropdown an ajax call is made rather than form submit to avoid refreshing the page.
			var options = 
		    	{	
					success: function(responseText, statusText, xhr ) 
							{
								var responseString = new String(responseText);
								var responsesArr = responseString.split("|");
								if(responsesArr[4] == "confirmation")
								{	
									removePageGreyOut();
									deleteDocumentId = documentId;
									$(".overlay").launchOverlay($(".alert-box-delete"), $(".exit-panel"), "350px", null, "onReady");
								}else{
									removePageGreyOut();
									$(".messagediv").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
									$(".messagediv").addClass(responsesArr[4]);
									$(".messagediv").show();
								}
							},
							error:function (xhr, ajaxOptions, thrownError)
							{                     
								showErrorMessagePopup();
								removePageGreyOut();
							}
						  };
						$(document.acceleratorForm).ajaxSubmit(options);
						return false;
		}
}

/* This will execute when upload button is clicked
Updated Method in R4*/
function uploadDocument(formAction)
{
		document.getElementById("next_action").value = "documentupload";
		document.acceleratorForm.action = formAction;
}

//This will execute when Filter Button tab is clicked
function displayFilter(formaction) {
    var isValid = true;
  $("input[type='text']").each(function(){
        if($(this).attr("validate")=='calender'){
              if(verifyDate(this)){
                    var fromDate = $("#modifiedfrom").val();
                    var toDate = $("#modifiedto").val();
                    if (Date.parse(toDate) < Date.parse(fromDate)) {
                          $("#dateRange").html('! This range is not valid');
                          isValid = false;
                          return false;
                    }
              }else{
                isValid = false;
              }
        }
  });
  if(isValid){
     pageGreyOut();
     $("#next_action").val('filterdocuments');
     document.acceleratorForm.action = formaction
                         + '&removeNavigator=true';
     document.acceleratorForm.submit();
  }
}

//This will execute when Clear Filter button is clicked
function clearFilter(form, sharedFlag, providerSet, agencySet) {
	document.getElementById("filtercategory").value = "";
	document.getElementById("modifiedfrom").value = "";
	document.getElementById("modifiedto").value = "";
	document.getElementById("filtersamplecategory").value = "";
	document.getElementById("filtersampletype").value = "";
	document.getElementById("filtertype").value = "";
	$('#filterTypeDiv').hide();
	$('#filterSampleCatDiv').hide();
	$('#filterSampleTypeDiv').hide();
	providerSet = providerSet.replace('[]', '');
	agencySet = agencySet.replace('[]', '');
	if (((providerSet != 'null') || (agencySet != 'null'))
			&& (($.trim(providerSet) != "") || ($.trim(agencySet) != ""))) {
		$("#sharedDiv").show();
		$('#providerDiv').show();
		$('#agencyDiv').show();
		updateProviderAndAgency(providerSet, agencySet);
		if (sharedFlag != "shared" && sharedFlag != "unshared") {
			$("#rdoBoth").attr('checked', true);
		}
		if (sharedFlag == "unshared") {
			$('#providerDiv').hide();
			$('#agencyDiv').hide();

		}
	}
	$("input[type='text']").each(function() {
		if ($(this).attr("validate") == 'calender') {
			$(this).parent().next().html("");
		}
	});
}

/*
 * This will execute when Previous,Next.. is clicked for pagination Updated
 * Method in R4
 */
function paging(pageNumber){
		var previousPage = document.getElementById("pageIndex").value;
		document.getElementById("pageIndex").value = pageNumber;
		document.acceleratorForm.action=originalFormAction+"&removeNavigator=true&nextPage="+pageNumber;
		document.acceleratorForm.submit();
}

/* This will execute when any Column header on grid is clicked for sorting
Updated Method in R4*/
function sort(columnName){
		var sortType = document.getElementById("sortType");
		if(sortType.value =="asc"){
			sortType.value = "desc";
		}
		else{
			sortType.value = "asc";
		}
		var sortBy = document.getElementById("sortBy");
		sortBy.value = columnName;
		 $("#next_action").val('filterdocuments');
		document.acceleratorForm.action=originalFormAction+"&removeNavigator=true&sortBy="+columnName+"&sortType="+sortType.value;
		document.acceleratorForm.submit();
}

//This function checks if the page is invoked for Organization Shared Documents screen and sets next actions and Page header in that case.
function viewOrganizationSharedDocScreen(){
		if( $("#isOrganizationSharesDoc").val()!= '' && $("#isOrganizationSharesDoc").val() == "true"){
			document.getElementById("pageHeader").innerHTML="Shared Documents";
			document.getElementById("descriptionMsg").style.display = "none";
			document.getElementById("helpIcon").style.display = "none";
			originalFormAction = originalFormAction +"&action=documentVault";
		}
}

//This will execute when "Sample" option is selected from Document Category filter
function getFilterSampleCategory() {
		var url = $("#contextPathSession").val()+"/GetContent.jsp?&category=samplecategory";
		postRequestForFilterSampleCategory(url);
}

// This will post the above servlet request and will get the response
function postRequestForFilterSampleCategory(strURL) {
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
				updateFilterSampleCategory(xmlHttp.responseText);
			}
		}
		xmlHttp.send(strURL);
}

// This will get the actual sample document category and type response and 
//will populate the corresponding drop downs with the response
function updateFilterSampleCategory(str) {
		var catString = str.split("|");
		var catSelectbox = document.getElementById("filtersamplecategory");
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
		$("#filtersampletype").attr("disabled", "disabled");
}

// This will execute when any option is selected from filter Sample Document Category drop down
function selectFilterSampleCategory() {
		if($('#filtersamplecategorycategory').val() == ""){
			$("#filtersampletype").attr("disabled", "disabled");
		}
		else{
			$("#filtersampletype").removeAttr("disabled");
		}
}




//This will execute when any servlet call is made
function postRequestFilterDocType(strURL) {
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
			updateFilterDocType(xmlHttp.responseText);
		}
	};
	xmlHttp.send(strURL);
}

//This will execute to get filter document type for filter document category
function updateFilterDocType(str) {
	var n = str.split("|");
	var selectbox = document.getElementById("filtertype");
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

//This method will make an Ajax request when any servlet call is made for fetching doc type
function postRequestFetchDocType(strURL) {
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
			updatepageFetchDocType(xmlHttp.responseText);
		}
	};
	xmlHttp.send(strURL);
}

//This will execute to get filter document type for filter document category
function updatepageFetchDocType(str) {
	var n = str.split("|");
	var selectbox = document.getElementById("filtersampletype");
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

//This will get the Document type list for selected Document category
function getDocumentTypeList(category, userOrg) {
	pageGreyOut();
	var url = $("#contextPathSession").val() + "/GetContent.jsp?selectedInput="
			+ category + "&organizationId=" + userOrg;
	postRequestFilterDocType(url);
	//$.unblockUI();
	removePageGreyOut();
}

//This will post the above servlet request and will get the response
function postRequestForFilterSampleType(strURL) {
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
				updateFilterSampleType(xmlHttp.responseText);
			}
		}
		xmlHttp.send(strURL);
}

// This will get the actual sample document category and type response and 
//will populate the corresponding drop downs with the response
function updateFilterSampleType(str) {
		var catString = str.split("|");
		var catSelectbox = document.getElementById("filtersampletype");
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
			if(""!=trim(catString[i])){
			catSelectbox.options.add(optn);
			}
		}
}

// Js function for contract budget review task jsp
function trim(stringToTrim) {
		return stringToTrim.replace(/^\s+|\s+$/g,"");
}

/* This will execute on load of jsp to enable disable unshareByProvider button based on document shared status
Updated Method in R4*/
function checkShareStatus(shareStatus, providerSet, agencySet) {
	if (null != document.getElementById("unshareByProvider")) {
		if ('null' != shareStatus && shareStatus == "true") {
			document.getElementById("unshareByProvider").disabled = false;
			document.getElementById("unshareByProvider").className = "unShare";
		} else {
			if ((providerSet.replace('[]', '') == '' && agencySet.replace('[]','') == '')
					|| (providerSet == 'null' && agencySet == 'null')) {
				document.getElementById("unshareByProvider").disabled = true;
				$("#sharedDiv").hide();
				$("#providerDiv").hide();
				$("#agencyDiv").hide();
			}
		}
	}
}

/* This will execute when any check box is clicked and will enable disable Share and UnshareAll button accordingly
Updated Method in R4*/
function enabledisablebutton(shareStatus, val) {
	if (null != document.getElementById("shareDoc")
			&& null != document.getElementById("unshareAll")) {

		var chks = document.getElementsByName('check');
		var hasChecked = false;
		var unShareEnable = true;
		for ( var i = 0; i < chks.length; i++) {
			if (chks[i].checked) {
				var shareCol = $(chks[i]).parent().parent().find(
						":nth-child(7)");
				if (shareCol.find("a").size() > 0)
					shareCol = shareCol.find("a").eq(0);
				shareStatus = shareCol.html();
				hasChecked = true;
				if (shareStatus == "Not Shared") {
					unShareEnable = false;
					break;
				}
			}
		}
		if (hasChecked) {
			document.getElementById("shareDoc").disabled = false;
			document.getElementById("shareDoc").className = "share";
			if (unShareEnable) {
				document.getElementById("unshareAll").disabled = false;
				document.getElementById("unshareAll").className = "unShare";
			} else {
				document.getElementById("unshareAll").disabled = true;
				document.getElementById("unshareAll").className = "unShare disable";
			}
		} else {
			document.getElementById("shareDoc").disabled = true;
			document.getElementById("shareDoc").className = "share disable";
			document.getElementById("unshareAll").disabled = true;
			document.getElementById("unshareAll").className = "unShare disable";
		}
	}
}


//This will execute when Share button is clicked
function shareDocument(formaction) {
	document.getElementById("next_action").value = "shareDocumentStep1";
}

// This will execute when UnShareAll button is clicked
function unshareAllDocument(formaction) {
	document.getElementById("next_action").value = "unsharedocumentall";
}

// This will execute when UnShareByProvider button is clicked
function unshareDocumentByProvider(formaction) {
	document.getElementById("next_action").value = "unsharedocumentbyprovider";
}

// This will execute when Shared link is clicked
function displaySharedDocuments(documentId, name, formaction) {
	document.getElementById("next_action").value = "displaySharedDocuments";
	document.acceleratorForm.action = formaction
			+ '&documentId='
			+ documentId
			+ '&documentName='
			+ name;
}

/* This will execute on load of jsp to populate provider and agency drop downs for filter based on document shared status
Updated Method in R4*/
function updateProviderAndAgency(providerSet, agencySet, selectedProv,
		selectedAgency) {
	if ('null' != providerSet) {
		var providers = providerSet.substring(0, providerSet.length);
		var provArray = providers.split("k3yv@lu3S3p@r@t0r");
		var selectboxprov = document.getElementById("provider");

		for ( var j = selectboxprov.options.length - 1; j >= 0; j--) {
			selectboxprov.remove(j);
		}
		var optn = document.createElement("OPTION");
		optn.text = "";
		optn.value = "";
		optn.setAttribute("title", "");
		selectboxprov.options.add(optn);
		if (provArray.length == 1 && provArray[0].length == 0) {
			document.getElementById("provider").disabled = true;
		}
		for ( var i = 0; i < provArray.length; i++) {
			var optn = document.createElement("OPTION");
			if (i > 0) {
				provArray[i] = provArray[i].substring(0, provArray[i].length);
			}
			optn.text = provArray[i];
			optn.value = provArray[i];
			optn.setAttribute("title", provArray[i]);
			if (provArray[i] == selectedProv) {
				optn.selected = "selected";
			}
			selectboxprov.options.add(optn);
		}
	}
	if ('null' != agencySet) {
		var agencies = agencySet.substring(1, agencySet.length - 1);
		var agencyArray = agencies.split(",");
		var selectboxagency = document.getElementById("agency");

		for ( var j = selectboxagency.options.length - 1; j >= 0; j--) {
			selectboxagency.remove(j);
		}
		var optn = document.createElement("OPTION");
		optn.text = "";
		optn.value = "";
		optn.setAttribute("title", "");
		selectboxagency.options.add(optn);
		if (agencyArray.length == 1 && agencyArray[0].length == 0) {
			document.getElementById("agency").disabled = true;
		}
		for ( var i = 0; i < agencyArray.length; i++) {
			var optn = document.createElement("OPTION");
			if (i > 0) {
				agencyArray[i] = agencyArray[i].substring(1,
						agencyArray[i].length);
			}
			optn.text = unescape(agencyArray[i]);
			optn.value = unescape(agencyArray[i]);
			optn.setAttribute("title", agencyArray[i]);
			if (agencyArray[i] == selectedAgency) {
				optn.selected = "selected";
			}
			selectboxagency.options.add(optn);
		}
	}
	if ('null' == agencySet && 'null' == providerSet) {
	}
	
	
}

//This will execute when unshared radio button is clicked from filter
function removeProviderAndAgency() {
	var selectboxprov = document.getElementById("provider");
	for ( var j = selectboxprov.options.length - 1; j >= 0; j--) {
		selectboxprov.remove(j);
	}
	var selectboxagency = document.getElementById("agency");
	for ( var j = selectboxagency.options.length - 1; j >= 0; j--) {
		selectboxagency.remove(j);
	}
}

function showDiv(providerSet,agencySet){
	$("#sharedDiv").show();
	$('#providerDiv').show();
	$('#agencyDiv').show();
}

