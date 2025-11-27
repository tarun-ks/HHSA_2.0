<%@page import="com.nyc.hhs.model.Document"%>
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant,org.apache.commons.lang.StringEscapeUtils"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>
<link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
<script type="text/javascript" src="../js/provdocumentlist.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<script type="text/javascript"> 
var provFormAction;
// on load function to perform various checks on loading of jsp
$(document).ready(function() {
	provFormAction = document.provform.action;	
	if($("#isOrganizationSharesDoc").val()!= '' && $("#isOrganizationSharesDoc").val() == "true"){
		provFormAction = provFormAction +"&action=documentVault";
		$('select').each(function() {
			var selectBoxId = $(this).attr("id");
			if(selectBoxId.match("^actions")) {
		 		var lastValue = $('#'+selectBoxId+' option:last-child').val();
		 		if(lastValue=='Delete Document'){
					$('#'+selectBoxId).find('option:last').remove();//("selected","selected");
		 		}
		 	}
		});
	}
	
	
	// Start of R4 Document Vault changes
	var docsOriginator  = '${param.docOriginator}';
	if(docsOriginator == 'city_org'){
		$("#filtertypediv").hide();
	}
	
	
	if($('#filtersamplecategory').val() == ""){
		$("#filtersampletype").attr("disabled", "disabled");
	}
	else{
		$("#filtersampletype").removeAttr("disabled");
	}
 	if('<%=((Document)request.getAttribute("document")).getDocCategory()%>' == "Sample Document"){
 		$("#filtertypediv").hide();
 		$('#filterSampleCatDiv').show();
		$('#filterSampleTypeDiv').show();
 	}
 	else if('<%=((Document)request.getAttribute("document")).getDocCategory()%>' == "Solicitation"){
		$('#filtertypediv').show();
		$('#filterSampleCatDiv').hide();
		$('#filterSampleTypeDiv').hide();
 	}
 	else{
 		$('#filterSampleCatDiv').hide();
		$('#filterSampleTypeDiv').hide();
 	}
 	
 	// End of R4 Document Vault changes 
	
	
	if(document.getElementById("profiltercategory").value!="")
	{	
		// Start of R4 Document Vault changes
		if(docsOriginator == 'city_org' && '<%=((Document)request.getAttribute("document")).getDocCategory()%>' != "Solicitation"){
			$("#filtertypediv").hide();
		}
		else{
		$("#filtertypediv").show();
		}
		// End of R4 Document Vault changes 
		document.getElementById("filtertype").disabled=false;
	}else{
		document.getElementById("filtertype").disabled=true;
	}
	
 	if("null" != '<%= request.getAttribute("message")%>' && '<%= request.getAttribute("messageType")%>' !="confirmation"){
		$(".messagediv").html('${message}'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		$(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
		$(".messagediv").show();
		<%request.removeAttribute("message");%>

	}
	if('<%=request.getAttribute("messageType")%>' == "confirmation")
	{
		$(".overlay").launchOverlay($(".alert-box-delete"), $(".exit-panel"), "350px", null, "onReady");
	}
		
checkShareStatus('<%=request.getAttribute("sharedFlag")%>');
updateProviderAndAgency('<%=request.getAttribute("providerSet")%>','<%=request.getAttribute("agencySet")%>','<%=((Document)request.getAttribute("document")).getFilterProviderId()%>',
'<%=((Document)request.getAttribute("document")).getFilterNYCAgency()%>');

// This will execute when delete document option is selected from drop down
$('#deleteDoc').click(function() {
	document.provform.action = provFormAction+'&documentId='+'<%=request.getAttribute("documentId")%>'+'&removeNavigator=true&next_action=<%=request.getAttribute("next_action")%>';
	document.provform.submit();
});

// This will execute when upload button is clicked
$('#uploadDoc').click(function() {
	pageGreyOut();
    uploadDocument(provFormAction);
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
	$(".overlay").launchOverlay($(".alert-box"), $(".exit-panel.upload-exit"), "850px", null, "onReady");
	$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
	$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
	//$.unblockUI();
	removePageGreyOut();
	},
	error:function (xhr, ajaxOptions, thrownError)
	{                     
		showErrorMessagePopup();
		removePageGreyOut();
	}
	};
	$(document.provform).ajaxSubmit(options);
	return false;
});

// This will execute when any option is selected for filter Document Category
/**
$('#profiltercategory').change(function() {
	if(filterCategoryForProvider1()){
	getContentForProvider();
	}	
});	
*/


// This will execute when Share button is clicked
$('#shareDoc').click(function() {
	pageGreyOut();
    shareDocument(provFormAction);
	var options = 
    	{	
			success: function(responseText, statusText, xhr ) 
					{
						var $response=$(responseText);
                        var data = $response.contents().find(".overlaycontent");
                            	$("#tab3").empty();
								$("#tab4").empty();
								$("#tab5").empty();
								$("#tab6").empty();
                        if(data != null && data != ''){
                        	$("#tab3").html(data.detach());
						}
						$("#sharelabel").html("- Step 1");
						var overlayLaunchedTemp = overlayLaunched;
						var alertboxLaunchedTemp = alertboxLaunched;
						$("#overlayedJSPContent").html($response);
						overlayLaunched = overlayLaunchedTemp;
						alertboxLaunched = alertboxLaunchedTemp;
						$('#sharewiz').removeClass('wizardUlStep1')
						.removeClass('wizardUlStep2').removeClass('wizardUlStep3')
						.removeClass('wizardUlStep4').addClass('wizardUlStep1');
						$(".overlay").launchOverlay($(".alert-box-sharedoc"), $(".exit-panel"), "850px", null, "onReady");
						//$.unblockUI();
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.provform).ajaxSubmit(options);
				return false;
			});	
			
		// This will execute when unshareAll button is clicked
		$('#unshareAll').click(function() {
			pageGreyOut();
    		unshareAllDocument(provFormAction);
			var options = 
    			{	
				   	success: function(responseText, statusText, xhr ) 
					{
						var $response=$(responseText);
                        var data = $response.contents().find(".overlaycontent");
                        	$("#unshareall").empty();
                        if(data != null || data != ''){
                        	$("#unshareall").html(data.detach());
						}
						var overlayLaunchedTemp = overlayLaunched;
						var alertboxLaunchedTemp = alertboxLaunched;
						$("#overlayedJSPContent").html($response);
						overlayLaunched = overlayLaunchedTemp;
						alertboxLaunched = alertboxLaunchedTemp;
						$(".overlay").launchOverlay($(".alert-box-unshareall"), $(".exit-panel"), "850px",null, "onReady");
						//$.unblockUI();
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.provform).ajaxSubmit(options);
				return false;		
		});	
		
		// This will execite when unshareByProvider button is clicked
		$('#unshareByProvider').click(function() {
			pageGreyOut();
    		unshareDocumentByProvider(provFormAction);
			var options = 
    			{	
				   	success: function(responseText, statusText, xhr ) 
					{
						var $response=$(responseText);
                        var data = $response.contents().find(".overlaycontent");
                        $("#unshareprovider").empty();
                        if(data != null || data != ''){
                        	$("#unshareprovider").html(data.detach());
						}
						var overlayLaunchedTemp = overlayLaunched;
						var alertboxLaunchedTemp = alertboxLaunched;
						$("#overlayedJSPContent").html($response);
						overlayLaunched = overlayLaunchedTemp;
						alertboxLaunched = alertboxLaunchedTemp;
						$(".overlay").launchOverlay($(".alert-box-unsharebyprovider"), $(".exit-panel"), "850px", "500px","onReady");
						//$.unblockUI();
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.provform).ajaxSubmit(options);
				return false;		
		});	
		
		// This will execute when Shared link is clicked
		$('.linkclass').click(function() {
			pageGreyOut();
			var linkId = $(this).attr('id'); 
			var name = $(this).attr('name');
    		displaySharedDocuments(linkId, name, provFormAction);
    		var options = 
    			{	
				   	success: function(responseText, statusText, xhr ) 
					{
						var $response=$(responseText);
                        var data = $response.contents().find(".overlaycontent");
                        $("#displayshared").empty();
                        if(data != null || data != ''){
                        	$("#displayshared").html(data.detach());
						}
						$("#removeprovlabel").html(name);
						var overlayLaunchedTemp = overlayLaunched;
						var alertboxLaunchedTemp = alertboxLaunched;
						$("#overlayedJSPContent").html($response);
						overlayLaunched = overlayLaunchedTemp;
						alertboxLaunched = alertboxLaunchedTemp;
						$(".overlay").launchOverlay($(".alert-box-removeselectedprovs"), $(".exit-panel"), "850px", "400px", "onReady");
						//$.unblockUI();
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{                     
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
				$(document.provform).ajaxSubmit(options);
				return false;		
		});	
		// This will execute when any value is selected for Shared - Unshared radio button in Filter Documents tab
		$("input[type='radio']").click(function() {
			if($(this).val()=="unshared")
			{
				$("#agencyDiv").hide();
				$("#providerDiv").hide();
				removeProviderAndAgency();
			}    
			else
			{
				$("#agencyDiv").show();
				$("#providerDiv").show();
				updateProviderAndAgency('<%=request.getAttribute("providerSet")%>','<%=request.getAttribute("agencySet")%>');    
			}
		});
		
		// Start of R4 Document Vault changes
		//This will execute when any option is selected for filter Document Category
		$('#filtersamplecategory').change(function() {
			if(samplefilterCategoryForCity()){
			getFilterSampleTypeForCity();
			}	
		});	
		// End of R4 Document Vault changes
		
		

		
});

// This method will get the filter document category and 
//will return the list of document types through servlet call
function getContentForProvider() {
	pageGreyOut();
	var selectedInput = document.getElementById("profiltercategory").value;
	// Start of R4 Document Vault changes
	var docOriginator  = '${param.docOriginator}';
	if(docOriginator == 'provider_org'){
		var url = $("#contextPathSession").val()+"/GetContent.jsp?selectedInput=" + selectedInput+"&organizationId=<%=ApplicationConstants.PROVIDER_ORG%>&sharedTypeRequest=true";
	}
	else{
		var url = $("#contextPathSession").val()+"/GetContent.jsp?selectedInput=" + selectedInput+"&organizationId=<%=ApplicationConstants.AGENCY_ORG%>";
	}
	// End of R4 Document Vault changes 
	
	postRequest(url);
	//$.unblockUI();
	removePageGreyOut();
}

// Start of R4 Document Vault changes: passing identifier parameter to the functions to indentify whether its shared document for city/provider/agency
function sharedFilter(docsOriginator){
	if(docsOriginator == 'city_org'){
		getContent();
	}
	else if(filterCategoryForProvider()){
		getContentForProvider();
		}
}
// End of R4 Document Vault changes 

// Start of R4 Document Vault changes
//This method will get the filter document category for city filter and 
//will return the list of document types through servlet call
function getContent() {
	var selectedInput = $("#profiltercategory").val();
	selectedInput = selectedInput.replace(/&/g, "$");
	if("Sample Document" == selectedInput){
		getFilterSampleCategory();
		$('#filterSampleCatDiv').show();
		$('#filterSampleTypeDiv').show();
		$('#filtertypediv').hide();
		$("#filtersampletype").removeAttr("disabled");
		removePageGreyOut();
		return false;
	}if("Solicitation" == selectedInput){
		getDocumentTypeList(selectedInput, "city_org");
		$('#filtertypediv').show();
		$('#filtertypediv').show();
		document.getElementById("filtertype").disabled = false;
		$('#filterSampleCatDiv').hide();
		$('#filterSampleTypeDiv').hide();
		removePageGreyOut();
		return false;
	}
	else{
		document.getElementById("filtersamplecategory").value = "";
		document.getElementById("filtersampletype").value = "";
		$('#filterSampleCatDiv').hide();
		$('#filterSampleTypeDiv').hide();
		$('#filtertypediv').hide();
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

//This will execute when "Sample" option is selected from Document Category filter
function getFilterSampleCategory() {
		var url = $("#contextPathSession").val()+"/GetContent.jsp?&category=samplecategory";
		postRequestForFilterSampleCategory(url);
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

//This will post the above servlet request and will get the response
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



//This will execute when any option is selected from filter Sample Document Category drop down
function selectFilterSampleCategory() {
		if($('#filtersamplecategorycategory').val() == ""){
			$("#filtersampletype").attr("disabled", "disabled");
		}
		else{
			$("#filtersampletype").removeAttr("disabled");
		}
}

//This will execute when any option is selected from filter Sample Document Category drop down
function selectFilterSampleCategory() {
		if($('#filtersamplecategorycategory').val() == ""){
			$("#filtersampletype").attr("disabled", "disabled");
		}
		else{
			$("#filtersampletype").removeAttr("disabled");
		}
}

//This will execute when any option is selected for filter Document Category
function samplefilterCategoryForCity() {
	var e = document.getElementById('filtersamplecategory');
	var category = e.options[e.selectedIndex].value;
	if (category == null || category == "") {
		document.getElementById("filtersampletype").value = "";
		document.getElementById("filtersampletype").disabled = true;
		return false;
	} else {
		document.getElementById("filtersampletype").disabled = false;
		return true;
	}

}

//This method will get the filter document category and 
//will return the list of document types through servlet call
function getFilterSampleTypeForCity() {
	pageGreyOut();
	var selectedInput = document.getElementById("filtersamplecategory").value;
	var url = $("#contextPathSession").val()+"/GetContent.jsp?selectedInput=" + selectedInput+"&organizationId=<%=ApplicationConstants.PROVIDER_ORG%>";
	postRequestFetchDocType(url);
	//$.unblockUI();
	removePageGreyOut();
}

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

//This will execute when Clear Filter button is clicked
function clearFilter(docOriginator) {
	document.getElementById("profiltercategory").value = "";
	document.getElementById("filtertype").disabled = true;
	document.getElementById("filtertype").value = "";
	document.getElementById("provider").value = "";
	document.getElementById("agency").value = "";
	document.getElementById("modifiedfrom").value = "";
	document.getElementById("modifiedto").value = "";
	document.getElementById("filtersamplecategory").value = "";
	 document.getElementById("filtersampletype").value = "";
	if(docOriginator == 'city_org'){
		 $('#filtertypediv').hide();
		 $('#filterSampleCatDiv').hide();
		 $('#filterSampleTypeDiv').hide();
	}
	$("input[type='text']").each(function(){
		 if($(this).attr("validate")=='calender'){
			 $(this).parent().next().html("");
		 }
	});
}
//End of R4 Document Vault changes
</script>


<!-- Body Wrapper Start -->
	<form id="myform" action="<portlet:actionURL/>" method ="post" name='provform'>
	<input type="hidden" name="next_action" value="" id="next_action">
	<input type="hidden" name="documentOriginator" value="${documentOriginator}" >
	<input type="hidden" name="cityUserSearchProviderId" value="${cityUserSearchProviderId}" >
			<c:choose>
    			<c:when test="${org_type == 'city_org'}">
    				<h2 id="pageHeader">Document Vault</h2>
					<div class='hr'></div>
					<span id="descriptionMsg">Manage documents loaded by your organization.</span>
					<div></div>
					<div class="messagediv" id="messagediv"></div>
    			</c:when>
    			<c:otherwise>
    				<h2 id="pageHeader">Shared Documents</h2>
					<div class='hr'></div>
					<div class="messagediv" id="messagediv"></div>
    			</c:otherwise>
    		</c:choose>
			<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S030_PAGE, request.getSession())){
				
				Document loDocBean = (Document)request.getAttribute("document"); %>
				<!-- Container Starts -->
				<!--Filter and Reassign section starts -->
				<div class="taskButtons">
					<%if(request.getAttribute("filterStatus") == "filtered"){%>
					 <input type="button" value="Filtered" title="Filtered" id= "filterbutton" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');"/>	
				<% } else{%>
					<input type="button" value="Filter Documents" title="Filter Documents" id= "filterbutton" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');"/>
				<%}%>

			<!-- Popup for Filter Task Starts -->
				<div id="documentValuePop" class='formcontainer'>
					<div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" title="Close">X</a></div>
					<div class='row'>
						<span class='label'>Document Category:</span>
						<span class='formfield'>
							<!--Start of R4 Document Vault changes-->
							<select id = "profiltercategory" name="filtercategory" class="terms" onchange="sharedFilter('${param.docOriginator}')">
							<!--End of R4 Document Vault changes -->
								<c:forEach var="category" items="${document.categoryList}" >
									<%String selected = "";%>
									<c:if test="${category==document.filterDocCategory}">
										<%selected = "selected";%>										
									</c:if>
									<option value="<c:out value="${category}"/>" <%=selected%> title="${category}"><c:out value="${category}"/></option>
								</c:forEach>
							</select>
						</span>
					</div>
					<div class='row' id="filtertypediv">
						<span class='label'>Document Type:</span>
						<span class='formfield'>
							<select id = "filtertype" name="filtertype" class="input">
								<c:forEach var="type" items="${document.typeList}" >
									<%String selected = "";%>
									<c:if test="${type==document.filterDocType}">
										<%selected = "selected";%>										
									</c:if>
									<option value="<c:out value="${type}"/>" <%=selected%> title="${type}"><c:out value="${type}"/></option>
								</c:forEach>
							</select>
						</span>
					</div>
					
					<!--Start of R4 Document Vault changes-->
					<div class="row" id="filterSampleCatDiv" style="display:none">
						<span class="label wrapText">Sample Document Category:</span> 
						<span class="formfield">
							<select id="filtersamplecategory" name="filtersamplecategory" onchange="javascript:selectFilterSampleCategory();">
							 <option value="" > </option>
								<c:forEach var="category" items="${document.sampleCategoryList}">
									<c:set var="selected" value=""></c:set>
									<c:if test="${category eq document.sampleCategory}">
										<c:set var="selected" value="selected"></c:set>
									</c:if>
									<option value="<c:out value="${category}"/>" ${selected} title='${category}'>
									<c:out value="${category}" /></option>
								</c:forEach>
							</select> 
						</span>
						<span class="error"></span>
					</div>
					<div class="row" id="filterSampleTypeDiv" style="display:none">
						<span class="label wrapText">Sample Document Type:</span> 
						<span class="formfield">
							<select id="filtersampletype" name="filtersampletype">
								 <option value="" > </option>
								<c:forEach var="type" items="${document.sampleTypeList}">
									<%String selected = "";%>
										<c:if test="${type==document.sampleType}">
											<%selected = "selected";%>
										</c:if>
									<option value="<c:out value="${type}"/>" <%=selected%> title="${type}">
									<c:out value="${type}" /></option>
								</c:forEach>
							</select> 
						</span>
						<span class="error"></span>
					</div>
					<!--End of R4 Document Vault changes -->
					
					<div class='row' id="providerDiv" style="display:none">
						<span class='label'>Provider:</span>
						<span class='formfield'>
							<select id="provider" name="provider" class="input">
							</select>
						</span>
					</div>
					<div class='row' id="agencyDiv" style="display:none">
						<span class='label'>NYC Agency:</span>
						<span class='formfield'>
							<select id="agency" name="agency" class="input">
							</select>
						</span>
					</div>
					<div class='row'>
						<span class='label'>Modified from:</span>
						<span class='formfield'>
							<input type="text" style='width:78px;' name="modifiedfrom" id='modifiedfrom' value="${document.filterModifiedFrom}"/>
							
					      	<img title="Modified From Date" alt="Modified From Date" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('modifiedfrom',event,'mmddyyyy');return false;"/>
						</span>
						<span class="error" id="datevalidate"></span>
					</div>
					<div class='row'>
						<span class='label'>Modified To:</span>
						<span class='formfield'>
							<input type="text" style='width:78px;' name="modifiedto" id='modifiedto' value="${document.filterModifiedTo}"/>
							
					      	<img title="Modified To Date" alt="Modified To Date" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('modifiedto',event,'mmddyyyy');return false;"/>
						</span>
						<span class="error" id="dateRange"></span>
					</div>
					<div class="buttonholder">
						<!--Start of R4 Document Vault changes-->
						<input type="button" id="clearfilter" title="Clear Filters" value="Clear Filters" onclick="clearFilter('${param.docOriginator}')" class="graybtutton"/>
						<!--End of R4 Document Vault changes -->
						<input type="button" value="Filter" title="Filter" id="filter" onclick="displayFilter(provFormAction)"/>
					</div> 
				</div>
				<!-- Popup for Filter Task Ends -->
				</div>
				  <div class='clear'></div>			
		
			<!--Filter and Reassign section ends -->
	
			<!-- Form Data Starts -->
			<div id="mymain">
			<!-- Grid Starts -->
			<div  class="tabularWrapper">
				<st:table objectName="documentList"  cssClass="heading"
					alternateCss1="evenRows" alternateCss2="oddRows" pageSize='<%=(Integer)session.getAttribute("allowedObjectCount")%>'>
					<st:property headingName="Document Name" columnName="docName" align="center" sortType="docName" sortValue="asc" size="30%">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameExtension" />
					</st:property>
					<st:property headingName="Document Type" columnName="docType" sortType="docType" sortValue="asc" align="center" size="30%"  />
					<st:property headingName="Modified" columnName="date"
						align="right" size="15%" sortType="date" sortValue="asc"/>
					<st:property headingName="Actions" columnName="actions"  align="center" size="25%" >
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.SharedDocumentActionExtension" />
					</st:property>
				</st:table>
			</div>
			<!-- Grid Ends -->
			</div>
			<input type="hidden" name="hiddenDocCategory" value='<%=loDocBean.getFilterDocCategory()%>' />	
			<input type="hidden" name="hiddenDocType" value='<%=loDocBean.getFilterDocType()%>'/>
			<input type="hidden" name="hiddenFilterModifiedFrom" value='<%=loDocBean.getFilterModifiedFrom()%>' />
			<input type="hidden" name="hiddenFilterModifiedTo" value='<%=loDocBean.getFilterModifiedTo()%>' />
			<input type="hidden" name="hiddenFilterProviderId" value='<%=loDocBean.getFilterProviderId()%>'/>
			<input type="hidden" name="hiddenFilterNYCAgency" value="<%=loDocBean.getFilterNYCAgency()%>"/>
			<input type="hidden" name="hiddenDocShareStatus" value='<%=loDocBean.getDocSharedStatus()%>' />
			<input type="hidden" name="provider" value='${ownerProviderId}' />					
			<!-- Form Data Ends -->
			
			
	</form>			

		<c:if test="${param.next_action eq 'openProviderView' or lsAction eq  'openProviderView'}">
			<input type="hidden" name="action" value="documentVault" id="searchDocumentVaultId"/>
		</c:if>
		<input type="hidden" id="isOrganizationSharesDoc" name="isOrganizationSharesDoc" value="${isOrganizationSharesDoc}" />
		<div id="overlayedJSPContent" style="display:none"></div>
		<% } else {%>
	   		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	   	<%} %>
