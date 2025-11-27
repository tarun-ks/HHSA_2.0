var data = null;
var provFormAction;
//On ready of the document
$(document).ready(
		function() {
			
			$("#filter").click(function(){
				documentFilter();
			});
			$("#clearfilter").click(function(){
				reset();
			});
			if(document.getElementById("documentCategoryFilter").value!="")
			{
				document.getElementById("documentTypeFilter").disabled=false;
			}else{
				document.getElementById("documentTypeFilter").disabled=true;
			}
		});
//sets the visibility filter pane		
function setVisibility(id, visibility) {
	$("#" + id).toggle();
	callBackInWindow("closePopUp");
}
//This function will be called for filtering the documents
function documentFilter() {
	$("#sampleform").attr("action",$("#sampleform").attr("action"));
	$("#nextAction").val("sampledocuments");
	document.sampleform.submit();
}
//resets the filter
function reset(){
	document.getElementById("documentCategoryFilter").value = "";
	document.getElementById("documentTypeFilter").disabled = true;
	document.getElementById("documentTypeFilter").value = "";
}
//This method is invoked when user click on any page link to navigate between the pages
function paging(pageNumber) {
	var url = $("#sampleform").attr("action");
	$("#nextAction").val("sampledocuments");
	$("#nextPageParam").val(pageNumber);
	document.sampleform.submit();
}
//This will execute when any option is selected from Document Category drop down 
//and will hide - unhide various div depending upon category selected
function selectCategory(form){
	var element = document.getElementById('documentCategoryFilter');
	var category = element.options[element.selectedIndex].value;
	var userOrg = $("#orgType").val();
	if(category == null || category == ""){ 
		document.getElementById("documentTypeFilter").value=""; 
		document.getElementById("documentTypeFilter").disabled = true; 
		return false; 
	}
	else{
		getDocumentTypeList(category, userOrg);
		document.getElementById("documentTypeFilter").disabled = false;
	} 
}
//This will get the Document type list for selected Document category
function getDocumentTypeList(category, userOrg) {
	pageGreyOut();
	var url = $("#contextPathSession").val()+"/GetContent.jsp?selectedInput=" + category
			+ "&organizationId=" + userOrg;
	postTypeRequest(url);
	//$.unblockUI();
	removePageGreyOut();
}
//This will process the document category request and will get the Document type response through servlet call
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
//This will update the Document Type drop down fetched through servlet call
function updatetypepage(str) {
	var documentTypeList = str.split("|");
	var selectbox = document.getElementById("documentTypeFilter");
	var icount;
	for (icount= selectbox.options.length - 1; icount >= 0; icount--) {
		selectbox.remove(icount);
	}
	if (null != documentTypeList) {
		var optn = document.createElement("OPTION");
		optn.text = "";
		optn.value = "";
		selectbox.options.add(optn);
		for ( var icount = 0; icount < documentTypeList.length - 1; icount++) {
			var optn = document.createElement("OPTION");
			optn.text = documentTypeList[icount];
			optn.value = documentTypeList[icount];
			selectbox.options.add(optn);
		}
	}
}
