<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.HHSComponentMappingConstant" %>
<portlet:defineObjects />
<script type="text/javascript">
suggestionVal = "";
isValid = false;
// On load - Disable sreen elements, validate insertion of Organizations in Listbox for error message and NYC ID and Provider Typeahead search.
$(document).ready(function(){
	//Scrollable
	//$('table').eq(2).scrollableTable(250)
	disableScreenElementsOnLoad();
	enableSubmitRequestCheck();
	typeHeadSearch($('#searchNycId'), $("#contextPathSession").val()+'/UserSearchServlet.jsp', "continue", null, null, "staffId");
	typeHeadSearch($('#provName'), $("#contextPathSession").val()+'/AutoCompleteServlet.jsp', "addProvider", null, null, "providerId");
	$('#addProvider').click(
            function (e) {
            	var select = document.getElementById("items");
            	var valueToInsert = $('#providerId').val();
            	var toInsertFlag = true;
            	$('#hiddenErrorDivOnSubmit').hide();
            	toInsertFlag = checkIfAlreadyAddedOnAddProvider(valueToInsert);
            	if($("#items option[value='"+valueToInsert+"']").length > 0)
            	{
            		 clearProviderSearchTypeAhead();
            	}
            	else
            	{
	            	if(toInsertFlag)
	            	{
	            		select.options[select.options.length] = new Option($('#provName').val(), $('#providerId').val());
	            		enableSubmitRequestCheck();
	            	}	
            	}
            	clearProviderSearchTypeAhead();
                enableRemoveProvideCheck();
                enableSubmitRequestCheck();
                e.preventDefault();
            });
	// Check to enable/disable Add/Remove Provider button on adding/removing Organizations from listbox
	$('#items').change(
			function (e) {
				enableRemoveProvideCheck();
			});
	// Check to enable/disable Add/Remove Provider button on adding/removing Organizations from listbox
	$('#items').click(
			function (e) {
				enableRemoveProvideCheck();
			});
	// Check to enable/disable Add/Remove Provider button on adding/removing Organizations from listbox adn check to enable/disable
	// Submitt Button on Removing Organizations from List Box
	 $('#removeProvider').click(
	            function (e) {
	                $('#items option:selected').remove();
	                $('#hiddenErrorDivOnSubmit').hide();
	            	$('#hiddenErrorDivOnAddProvider').hide();
	                enableRemoveProvideCheck();
	                enableSubmitRequestCheck();
	                e.preventDefault();
	            });
});

//Created for R4: This function processes the Submit Access Request and redirects request to controller
function submitAccessRequestAction()
{
	pageGreyOut();
	var toSubmitFlag = false;
	var select = document.getElementById("items");
	toSubmitFlag = checkIfAlreadyExist();
	if(toSubmitFlag)
		{
			for (var i = 0; i < select.options.length; i++) 
			{
				select.options[i].selected = true;
			}
			pageGreyOut();
			var v_parameter = "&" + $("#submitAccessRequestForm").serialize();
			var urlAppender = $("#submitAccessRequestUrl").val();
			jQuery.ajax({
				type : "POST",
				cache : false,
				url : urlAppender,
				data : v_parameter,
				success : function(data) {
					if (data != null) {
						bufferData="";
						bufferData=data;
							var msg = data.split("#k3yv@lu3S3p@r@t0r");
							if (msg[0] == "failure") {
								$("#transactionStatusDiv").html(msg[1]);
								$("#transactionStatusDiv").addClass('failed');
								$("#transactionStatusDiv").show();
								removePageGreyOut();
							} else if (msg[0] == "success") {
								document.getElementById('userDetailsContainer').innerHTML = msg[2];
								$("#transactionStatusDiv").html(msg[1]);
								$("#transactionStatusDiv").addClass('passed');
								$("#transactionStatusDiv").show();
								postSuccessfulSubmit();
							} 
						}
					},
				error : function(data, textStatus, errorThrown) {
					showErrorMessagePopup();
					 removePageGreyOut();
				}
			});
		}
	else
		{
			removePageGreyOut();
		}
}

// This method changes screen elements post successful submission of Access Request
function postSuccessfulSubmit()
{
	$('#clearProvider').attr('disabled', 'disabled');
	$('#provName').attr('value', '');
	$('#provName').attr('disabled', 'disabled');
	$('#addProvider').attr('disabled', 'disabled');
	$('#removeProvider').attr('disabled', 'disabled');
	$('#items option:selected').remove();
	$('#items').attr('disabled', 'disabled');
	$('#submitAccessRequest').attr('disabled', 'disabled');
	removePageGreyOut();
}

//This function selects all the items in Provider selection list box
function selectAllOptions()
{
	var select = document.getElementById("items");
	for (var i = 0; i < select.options.length; i++) {
		select.options[i].selected = true;
    }
}

//This function de-selects all the items in Provider selection list box
function deSelectAllOptions()
{
	var select = document.getElementById("items");
	for (var i = 0; i < select.options.length; i++) {
		select.options[i].selected = false;
    }
}

//This function validates on submitting a request for tagging to a provider that already has a pending request
function checkIfAlreadyExist()
{
	$('#hiddenErrorDivOnAddProvider').hide();
	$('#hiddenErrorDivOnSubmit').hide();
	var selectedProviders = [];
	$("#items option").each(function(i){
		selectedProviders[i] = $(this).val();
		});
	var existingProvidersList = [];
	var existingProviders = $("#existingProviderList").val();
	existingProviders = existingProviders.substring(1, existingProviders.length-1);
	existingProvidersList = existingProviders.split(', ');
	var found = true;
	for (var i=0;i<selectedProviders.length;i++)
	{
		found = ($.inArray( selectedProviders[i], existingProvidersList) == -1);
		if(!found)
			break;
	}
	if(!found)
		$('#hiddenErrorDivOnSubmit').show();
	return found;
}

//This function validates on adding same provider that is already added in list box
function checkIfAlreadyAddedOnAddProvider(toBeInsertedProvider)
{
	$('#hiddenErrorDivOnAddProvider').hide();
	$('#hiddenErrorDivOnSubmit').hide();
	var selectedProviders = [];
	$("#items option").each(function(i){
		selectedProviders[i] = $(this).val();
		});
	var foundInList = ($.inArray(toBeInsertedProvider, selectedProviders) == -1);
	if(!foundInList)
		$('#hiddenErrorDivOnAddProvider').show();
	return foundInList;
}

//This Function Clears Provider searc typeahead
function clearProviderSearchTypeAhead()
{
	$('#provName').attr('value', '');
	$('#addProvider').attr('disabled', 'disabled');
}

//This function checks whether or not to enable Remove Provider Button
function enableRemoveProvideCheck()
{
	var select = document.getElementById("items");
	if($('#items option').length==0)
		$('#removeProvider').attr('disabled', 'disabled');
	else if ($('#items option:selected').length<1)
		$('#removeProvider').attr('disabled', 'disabled');
	else if ($('#items option:selected').length>0)
		$('#removeProvider').removeAttr('disabled');
	else
		$('#removeProvider').removeAttr('disabled');
}

//This function disables the screen items on Page Load
function disableScreenElementsOnLoad()
{
	$('#hiddenErrorDivOnSubmit').hide();
	$('#hiddenErrorDivOnAddProvider').hide();
	$('#continue').attr('disabled', 'disabled');
	$('#provName').attr('disabled', 'disabled');
	$('#items').attr('disabled', 'disabled');
	$('#clearProvider').attr('disabled', 'disabled');
	$('#addProvider').attr('disabled', 'disabled');
	$('#removeProvider').attr('disabled', 'disabled');
	$('#fname').attr('disabled', 'disabled');
	$('#lname').attr('disabled', 'disabled');
	$('#mname').attr('disabled', 'disabled');
	$('#fnameTemp').attr('disabled', 'disabled');
	$('#lnameTemp').attr('disabled', 'disabled');
	$('#mnameTemp').attr('disabled', 'disabled');
	$('#submitAccessRequest').attr('disabled', 'disabled');
}

//This function clears the items on screen on Click of Clear Provider Button
function clearProvider()
{
	$('#clearProvider').attr('disabled', 'disabled');
	$('#searchNycId').removeAttr('disabled');
	$('#searchNycId').attr('value', '');
	$('#provName').attr('value', '');
	$('#provName').attr('disabled', 'disabled');
	$('#items').attr('disabled', 'disabled');
	selectAllOptions();
	$('#items option:selected').remove();
	$('#staffId').attr('value', '');
	$('#fname').attr('value', '');
	$('#mname').attr('value', '');
	$('#lname').attr('value', '');
	$("#existingProviderList").attr('value', '');
	$("#userDetailsTempContainer").show();
	$("#userDetailsContainer").hide();
	$('#hiddenErrorDivOnSubmit').hide();
	$('#hiddenErrorDivOnAddProvider').hide();
	enableSubmitRequestCheck();
}

//This function checks whether or not to enable Submit Request Button
function enableSubmitRequestCheck()
{
	if(($('#items option').length>0) && (null != $('#staffId').val() && $('#staffId').val().length>0))
    	$('#submitAccessRequest').removeAttr('disabled');
	else
		$('#submitAccessRequest').attr('disabled', 'disabled');
}

//This button on click of Continue button populates Provider details on screen
function fetchProviderDetailsOnContinue()
{
	pageGreyOut();
	var staffId = $("#staffId").val();
	var url = $("#getProviderUsrInfoResourceUrl").val() + "&staffId=" + staffId;
	var jqxhr = $
		.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(data) {
				bufferData="";
				bufferData=data;
				if (data != null) {
					var msg = data.split("#");
					if (msg[0] == "failure") {
						$("#transactionStatusDiv").html(msg[1]);
						$("#transactionStatusDiv").addClass('failed');
						$("#transactionStatusDiv").show();
					} else {
						document.getElementById('userDetailsContainer').innerHTML = data;
						$("#userDetailsTempContainer").hide();
						$("#userDetailsContainer").show();
						$('#searchNycId').attr('disabled', 'disabled');
						$('#provName').removeAttr('disabled');
						$('#items').removeAttr('disabled');
						$('#continue').attr('disabled', 'disabled');
						$('#clearProvider').removeAttr('disabled');
						enableSubmitRequestCheck();
					}
				}
				removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				removePageGreyOut();
				showErrorMessagePopup();
			}
		});
}
</script>
<% if(CommonUtil.getConditionalRoleDisplay(HHSComponentMappingConstant.PROVIDER_SETTINGS, request.getSession())){%>
<!-- resourceURL and hidden variable for ajax call -->
<portlet:resourceURL var='getProviderUsrInfoResourceUrl' id='getProviderUsrInfoResourceUrl' escapeXml='false'></portlet:resourceURL>
<input type='hidden' value='${getProviderUsrInfoResourceUrl}' id='getProviderUsrInfoResourceUrl' />
<div  class="portlet1Col">
	<div id="transactionStatusDiv" class=""></div>
	<h2>Provider Settings - Multiple Account Access Requests</h2>
	<div class='hr'></div>
	<c:if test="${message ne null}">
		<div class="${messageType}" id="messagediv" style="display:block">${message} <img
			src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
			class="message-close" onclick="showMe('messagediv', this)">
		</div>
		<div>&nbsp;</div>
	</c:if>
	<p style="text-align:left;">
		<span style="font-family:Verdana;font-size:12px;font-weight:normal;font-style:normal;text-decoration:none;color:#000000;">
			Use this page to submit access requests to multiple Provider Organizations on behalf of a Provider NYC ID.
		</span>
	</p>
	<div class="tabularCustomHead">Enter a Provider NYC ID</div>
	<div class="tabularContainer">
		<p style="text-align:left;">
			<span style="font-family:Verdana;font-size:12px;font-weight:normal;font-style:normal;text-decoration:none;color:#000000;">
				Use the text box below to find a Provider NYC ID (Johnsmith@provider.org).<br />
				Please note: The NYC ID must be associated with at least one approved and active Provider Organization account.
			</span>
		</p>
	   	<input type="text" size="128" style="width:35%;" maxlength="128" id="searchNycId">
	   	<input type="button" onclick="fetchProviderDetailsOnContinue();"  value="Continue" class="button" id="continue">
	   	<input type="button"  value="Clear Provider" onclick="clearProvider();" class="button" id="clearProvider">
	   	<div>&nbsp;</div>
	  	<div class="tabularCustomHead">NYC.ID User Information</div>
	   	<div id ="userDetailsTempContainer">
	   		<div class="formcontainer">
			   	<div class="row">
				      <span class="label">First Name:</span>
				      <span id="" class="formfield selectedservice"><input type="text" style="width:75%;" id="fnameTemp"></span>
				</div>
				<div class="row">
				      <span class="label">Middle Initial:</span>
				      <span id="" class="formfield selectedservice"><input type="text" size="1" style="width:8%;" id="mnameTemp"></span>
				</div>
				<div class="row">
				      <span class="label">Last Name:</span>
				      <span id="" class="formfield selectedservice"><input type="text" style="width:75%;" id="lnameTemp"></span>
				</div>
			</div>
			<p style="text-align:left;">
				<span style="font-family:Verdana;font-size:12px;font-weight:normal;font-style:normal;text-decoration:none;color:#000000;">
					The table below displays organizations that this NYC.ID currently has access to and organizations with pending access requests.
				</span>
			</p>
			<!-- Provider Details Grid -->
			<div class="tabularWrapper">
				<st:table  objectName="staffDetailsBeanList"  cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
					<st:property headingName="Provider Name" columnName="msOrganisationName"
						align="left" size="30%" />			
					<st:property headingName="Office Title" columnName="msOfficeTitle"
						align="right" size="25%" />
					<st:property headingName="Permission Level" columnName="msPermissionLevel"
						align="right" size="35%" />
				</st:table>
				<c:if test="${staffDetailsBeanList eq null or fn:length(staffDetailsBeanList) eq 0}">
					<div class="messagedivNycMsg noRecord" id="messagedivNycMsg">&nbsp;</div>
				</c:if>
				<div>&nbsp;</div>
			</div>
	   	</div>
		<div id='userDetailsContainer' style="display: none"></div>
	</div>                    
</div>
<portlet:resourceURL var='submitAccessRequestUrl' id='submitAccessRequestUrl' escapeXml='false'></portlet:resourceURL>
<input type='hidden' value='${submitAccessRequestUrl}' id='submitAccessRequestUrl' />
<form action="${submitAccessRequestUrl}" method="post" id="submitAccessRequestForm" name="submitAccessRequestForm">
	<div  class="portlet1Col">
		<div class="tabularContainer">
			<div class="formcontainer">
				<div class="row">
					<span class="label" style="padding:13px 5px 13px 0px">
						Type a Provider Name Here then click Add Provider:
					</span>
					<span id="" class="formfield selectedservice">
						<input type="text" class="input" style="width:100%;" name="provName" maxlength="60"  id="provName"/>
						<input type="hidden" name="providerId" id ="providerId">
						<input type="hidden" name="staffId" id ="staffId">
						<div class="taskButtons ">
							<input type="button" title="Add Provider" value="Add Provider" class="add" id = "addProvider">
						</div>
					</span>
				</div>
				<div class="row">
					<span class="label">
						Providers Added for Account Access:
					</span>
					<span id="" class="formfield selectedservice">
						<select multiple="multiple" style="width:100%;" class="multiselect" size="5" name="items" id="items"></select>
						<div class="taskButtons ">
							<input type="button" title="Remove Provider" value="Remove Provider" class="remove" id= "removeProvider">
						</div>
					</span>
					<div id = "hiddenErrorDivOnSubmit" name="hiddenErrorDivOnSubmit" style="float: left; margin-left: 5px; width: 28%;">
						<span>
							<p style="float: left; color: red;">There is existing access or a pending access request to one or more of the Providers you have selected. 
							You can remove duplicate Providers from your request by highlighting them and clicking the "Remove Provider" button above.</p>
						</span>
					</div>
					<div id = "hiddenErrorDivOnAddProvider" name="hiddenErrorDivOnAddProvider" style="float: left; margin-left: 5px; width: 28%;">
						<span>
							<p style="float: left; color: red;">! Provider is already selected</p>
						</span>
					</div>
				</div>
				
			</div>
		</div>
	</div>
	<div class="buttonholder" style="margin-right: 28px;">
		<input type="button" value="<< Back" title="Back" class="button" onclick="location.href='${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenancelanding&_nfls=false&app_menu_name=header_maintenance'"/>
		<input type="button" value="Submit Access Request" title="Submit Access Request"  name="submitAccessRequest" onclick="submitAccessRequestAction();"  id="submitAccessRequest" class="button" />
	</div>
	</form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>  