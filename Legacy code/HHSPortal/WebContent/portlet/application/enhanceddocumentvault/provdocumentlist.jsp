<!-- Initial Imports -->
<%@page import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
<%@page import="com.nyc.hhs.model.Document"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil"%>
<%@page import="com.nyc.hhs.constants.ComponentMappingConstant,org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.Map,java.util.HashMap,com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb,java.util.List, java.util.Iterator"%>
<%@page import="com.nyc.hhs.model.DocumentPropertiesBean"%>
<%@page import="com.nyc.hhs.util.ApplicationSession" %>
<!-- End -->
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<!-- Including Tag Libraries -->
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!-- End -->
<!-- Including Error page -->
<%@ page errorPage="/error/errorpage.jsp" %>
<!-- End -->
<!-- Portlets Url's -->
<portlet:defineObjects/>
<portlet:renderURL var="editProposal" escapeXml="false">
			<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProposalSummary" />
			<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ProposalDocuments" />
			<input type="hidden" name="render_action" id="render_action" value="procurementProposalDocumentList" />
			<input type="hidden" name="action" id="action" value="rfpRelease" />
</portlet:renderURL>

<portlet:resourceURL var="findSharedDocs" id="findSharedDocs" escapeXml="false"></portlet:resourceURL>
<portlet:resourceURL var="openTreeAjax" id="openTreeAjax" escapeXml="false"></portlet:resourceURL>
<portlet:resourceURL var="openTreeAjaxOrg" id="openTreeAjaxOrg" escapeXml="false"></portlet:resourceURL>
<portlet:resourceURL var="checkLinkage" id="checkLinkage" escapeXml="false"></portlet:resourceURL>
<portlet:resourceURL var="linkage" id="linkage" escapeXml="false"></portlet:resourceURL>
<portlet:resourceURL var="removeSession" id="removeSession" escapeXml="false"></portlet:resourceURL>
<portlet:renderURL var="checkLocking" id="checkLocking"></portlet:renderURL>
<!-- End -->
<!-- Including Style Sheets -->
<link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
<link rel="stylesheet" href="../css/style.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<c:if test="${org_type ne 'city_org'}">
 <% if(!(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S030_SECTION, request.getSession()) || 
	       			CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession()))){%> 
<style>
.showShareUnshare {
display: none;
}
</style>
   <%}%> 
   </c:if>
   <input type="hidden" id="findToolTip" value="${org_type}" />
<!-- End -->
<!-- Including javaScript Files -->
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/jstree.min.js"></script> 
<script type="text/javascript" src="../js/enhancedprovdocumentlist.js"></script>
<script>
var provFormAction;
var searchFormAction;
var deletesearchform;
var sharedFormInfo;
var findOrgDocForm;
var findshareddocform;
var hideShareUnshare = false;
var hideShareAgency = false;
var loggedInUserOrgType = '${org_type}';
$(document).ready(function() {
	$("#newfolderform").validate({
		rules: {
			folderName: {
				required: true,
				allowSpecialChar: ["A"," _-"],
				ValidateName:true,
				maxlength: 50
				}
		},
		messages: {
			folderName: {
				required: "<fmt:message key='REQUIRED_FOLDER_NAME'/>",
				allowSpecialChar:"<fmt:message key='ALPHANUMERIC_ALLOWED_DOCUMENT_NAME'/>",
				ValidateName:"Using This Folder Name Is Restricted",
				maxlength: "Folder Name cannot exceed 50 characters"
				    }
		},
		submitHandler: function(form){
			document.newfolderform.action = document.newfolderform.action+"&submit_action=createFolder&isAjaxCall=true";
			$(document.newfolderform).ajaxSubmit(options);
			pageGreyOut();
		},
		errorPlacement: function(error, element) {
		      error.appendTo(element.parent().parent().find("span.error"));
		}
	});
	hideShareUnshare = <%=CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S030_SECTION, request.getSession())%>;
	hideShareAgency = <%=CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())%>;
	$("#providerSharedOrgId").change(function (){
		document.providerInfo.action = document.providerInfo.action +"&providerId="+$(this).val();
		document.providerInfo.submit();
	});
	//combo box-for search Document Vault
	$("#filtertype").typeHeadDropDown({button:$("#combotable_button1"), optionBox: $("#dropdownul1")});
	$("#docTypeRec").typeHeadDropDown({button:$("#combotable_buttonForRecycleBin"), optionBox: $("#dropdownulForRecycleBin")});
	sharedFormInfo = $('#sharedPageOrg').val();
	$('#sharedPageOrgFolder').val(sharedFormInfo);
	provFormAction = document.provform.action;
	searchFormAction = document.searchform.action;
	deletesearchform = document.deleteform.action;
	<c:if test="${org_type eq 'city_org'}">
	findOrgDocForm = document.findorgdocform.action;
	</c:if>
	<c:if test="${org_type eq 'agency_org' || org_type eq 'provider_org'}">
	findshareddocform = document.findshareddocform.action;
	</c:if>
	var onAutocompleteSelect3 = function(value, data) {		
		document.getElementById("procurementTitle").value=value;
		document.getElementById("procurementId").value=data;
		isValid = true;
	};
	var onAutocompleteSelect4 = function(value, data) {		
		document.getElementById("awardepinTitle").value=value;
		isValid = true;
	};
	var onAutocompleteSelect6 = function(value, data) {		
		document.getElementById("invoiceNum").value=value;
		isValid = true;
	};
	var onAutocompleteSelect40 = function(value, data) {		
		document.getElementById("contractawardepinTitle").value=value;
		isValid = true;
	};
	var onAutocompleteSelectShareWith = function(value, data) {
		$('#sharedWith').val(value);
		$('#sharedWithValue').val(data);
		isValid = true;
	};
	var onAutocompleteSelect5 = function(value, data) {		
		document.getElementById("amendmentepinTitle").value=value;
		isValid = true;
	};
	var city_url  =$("#contextPathSession").val()+"/GetContent.jsp?&isFilter=true&requestingtype=city_find_org&headerClick=false";
	<c:if test="${org_type ne 'city_org'}">
	city_url = $("#contextPathSession").val()+"/GetContent.jsp?&isFilter=true&headerClick=false";
	</c:if>
	
	var options3 = {
		 	serviceUrl: $("#contextPathSession").val()+"/GetContent.jsp?&isProcurementTitle=true",
		   width: 252,
		   minChars:3,
		   maxHeight:100,
		   onSelect: onAutocompleteSelect3,
		   clearCache: true,
		   deferRequestBy: 0, //miliseconds
		   params: { city: $("#procurementTitle").val() }
		};
	var options4 = {
			
		 	serviceUrl: $("#contextPathSession").val()+"/GetContent.jsp?&isAwardepinTitle=true",
		   width: 252,
		   minChars:3,
		   maxHeight:100,
		   onSelect: onAutocompleteSelect4,
		   clearCache: true,
		   deferRequestBy: 0, //miliseconds
		   params: { city: $("#awardepinTitle").val() }
		};
	var options40 = {
			
		 	serviceUrl: $("#contextPathSession").val()+"/GetContent.jsp?&isContractAwardepinTitle=true",
		   width: 252,
		   minChars:3,
		   maxHeight:100,
		   onSelect: onAutocompleteSelect40,
		   clearCache: true,
		   deferRequestBy: 0, //miliseconds
		   params: { city: $("#contractawardepinTitle").val() }
		};
	var optionsForSharedWith = {
		 	serviceUrl: $("#contextPathSession").val()+"/GetContent.jsp?&isSharedWIth=true",
		   width: 245,
		   minChars:3,
		   maxHeight:100,
		   onSelect: onAutocompleteSelectShareWith,
		   clearCache: true,
		   deferRequestBy: 0, //miliseconds
		   params: { city: $("#sharedWith").val() }
		};
	var options5 = {
		 	serviceUrl: $("#contextPathSession").val()+"/GetContent.jsp?&getAmendmentEPin=true",
		   width: 252,
		   minChars:3,
		   maxHeight:100,
		   onSelect: onAutocompleteSelect5,
		   clearCache: true,
		   deferRequestBy: 0, //miliseconds
		   params: { city: $("#amendmentepinTitle").val() }
		};
	var options6 = {
		 	serviceUrl: $("#contextPathSession").val()+"/GetContent.jsp?&getInvoiceDetails=true",
		   width: 252,
		   minChars:3,
		   maxHeight:100,
		   onSelect: onAutocompleteSelect6,
		   clearCache: true,
		   deferRequestBy: 0, //miliseconds
		   params: { city: $("#invoiceNum").val() }
		};
	$('#sharedWith').autoCompleteOptionSave(optionsForSharedWith);
	$('#procurementTitle').autoCompleteOptionSave(options3);
	$('#awardepinTitle').autoCompleteOptionSave(options4);
	$('#contractawardepinTitle').autoCompleteOptionSave(options40);
	$('#amendmentepinTitle').autoCompleteOptionSave(options5);
	$('#invoiceNum').autoCompleteOptionSave(options6);
	

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

	//ends

			//varibale for todays`s date
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; //January is 0!

	var yyyy = today.getFullYear();
	if(dd<10){
	    dd='0'+dd;
	} 
	if(mm<10){
	    mm='0'+mm;
	} 
	var today = mm +'/' +  dd+'/' +yyyy; 
	//end
	//Form for find shared documents search
	$("#findshareddocform").validate({
		rules: {
			docName:{
				require_from_group: [1,".groupName"],
				allowSpecialChar: ["A","_,.\\\"\\\'\\\ -"],
				maxlength: 50
			},
			doctype_city:{
				require_from_group: [1,".groupName"],
				typeHeadDropDown: true
			},
			modifiedfrom5:{
				require_from_group: [1,".groupName"],
				minlength : 10,
				maxlength : 10,
				DateFormat : true,
				calenderRestrictFutureDate : true,
				DateRange : new Array("01/01/1800", today)
			},
			modifiedto5:{
				require_from_group: [1,".groupName"],
				minlength : 10,
				maxlength : 10,
				DateFormat : true,
				calenderRestrictFutureDate : true,
				DateRange : new Array("01/01/1800", today),
				DateToFrom: new Array("modifiedfrom5",false,"=")
			},
			modifiedfrom4:{
				require_from_group: [1,".groupName"],
				minlength : 10,
				maxlength : 10,
				DateFormat : true,
				calenderRestrictFutureDate : true,
				DateRange : new Array("01/01/1800", today)
			},
			modifiedto4:{
				require_from_group: [1,".groupName"],
				minlength : 10,
				maxlength : 10,
				DateFormat : true,
				calenderRestrictFutureDate : true,
				DateRange : new Array("01/01/1800", today),
				DateToFrom: new Array("modifiedfrom4",false,"=")
			}
		},
		messages: {
			docName:
		        	{
					require_from_group:"",
		    	  	allowSpecialChar:"<fmt:message key='REQUIRED_VALID_NAME'/>",
					maxlength: "<fmt:message key='INPUT_50_CHAR_NEW'/>"
					},
			doctype_city:{
					require_from_group:"",
					typeHeadDropDown: "! Please select a valid document type"
					},		
			modifiedfrom5: {
					require_from_group:"",
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
				}, 
			modifiedfrom4: {
					require_from_group:"",
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
					}, 
			modifiedto4: {
					require_from_group:"",
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					DateToFrom : "<fmt:message key='REQUIRED_VALID_DATE1'/>",
					calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
					},
			modifiedto5: {
					require_from_group:"",
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					DateToFrom : "<fmt:message key='REQUIRED_VALID_DATE1'/>",
					calenderRestrictFutureDate : "! Invalid Date. Please enter a date in the past"
			}
		},
		submitHandler: function(form){
			$('#messagediv').hide();
			findSharedDocumentSubmit(form);
		},
		invalidHandler: function(event, validator) {
			for(var i in validator.errorMap)
				{
					if(validator.errorMap[i].length>0)
						{
							$(".messagediv").hide();
						}
					else{
						var _message= "You must select at least 1 search criteria.";
						$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
				  		$(".messagediv").addClass("failed");
				  		$(".messagediv").show();
					}
				}
			
		},
		errorPlacement: function(error, element) {
			if(element.parent().parent().attr('class')== 'formfield modifieddaterange'){
				error.appendTo(element.parent().parent().find("span.error[for='"+$(element).attr("id")+"']"));
			}
			else{
		      error.appendTo(element.parent().find("span.error"));
			}
		}
		});
	//end
	
	//Form for document vault search
	$("#searchform").validate({
		rules: {
			docName: {
				require_from_group: [1,".groupName"],
				allowSpecialChar: ["A","_,.\\\"\\\'\\\ -"],
				maxlength: 50
				},
				
				doctype_city:{
				require_from_group: [1,".groupName"],
				typeHeadDropDown: true
				},
			shared:{
				require_from_group: [1,".groupName"]
				},
			linked:{
				require_from_group: [1,".groupName"]
				},
			sharedWith:{
				require_from_group: [1,".groupName"],
				autoCompleteOption: true
				},
			submittedfrom: {
				require_from_group: [1,".groupName"],
				minlength : 10,
				maxlength : 10,
				DateFormat : true,
				calenderRestrictFutureDate : true,
				DateRange : new Array("01/01/1800", today)
			},
			submittedfrom1: {
				require_from_group: [1,".groupName"],
				minlength : 10,
				maxlength : 10,
				DateFormat : true,
				calenderRestrictFutureDate : true,
				DateRange : new Array("01/01/1800", today),
				required : {
					depends: function(element) {
						if(($("#submittedfrom1").val() == "" && $("#submittedTo1").val()  == ""))
							{
							return true;
							}
												}
			}
			},
			submittedTo: {
				require_from_group: [1,".groupName"],
				minlength : 10,
				maxlength : 10,
				DateFormat : true,
				calenderRestrictFutureDate : true,
				DateRange : new Array("01/01/1800", today),
				DateToFrom: new Array("submittedfrom", false,"=")
			},
			submittedTo1: {
				require_from_group: [1,".groupName"],
				minlength : 10,
				maxlength : 10,
				DateFormat : true,
				calenderRestrictFutureDate : true,
				DateRange : new Array("01/01/1800", today),
				DateToFrom: new Array("submittedfrom1", false,"=")
			},
			procurementTitle:{
				autoCompleteOption: true,
				required : {
					depends: function(element) {
						if($('#linked').val()=="Procurement"||($('#linked').val()=="providerAward")||($('#linked').val()=="Award")||($('#linked').val()=="Proposal"))
							{
							return true;
							}
												}
			}
			},
			amendmentepinTitle:{
				autoCompleteOption: true,
				required : {
					depends: function(element) {
						if($('#linked').val()=="amendment")
							{
							return true;
							}
												}
			}
			},
			awardepinTitle:{
				autoCompleteOption: true,
				required : {
					depends: function(element) {
						if($('#linked').val()=="agencyAward"||$('#linked').val()=="Budget")
							{
							return true;
							}
												}
			}
			},
			contractawardepinTitle:{
				autoCompleteOption: true,
				required : {
					depends: function(element) {
						//Added for Release 6: Returned Payment linked entity search
						if($('#linked').val()=="Contract"||$('#linked').val()=="returnedPayment")
							{
							return true;
							}
						//Added for Release 6: Returned Payment linked entity search end
												}
			}
			},
			invoiceNum:{
				autoCompleteOption: true,
				required : {
					depends: function(element) {
						if($('#linked').val()=="Invoice")
							{
							return true;
							}
												}
			}
			}
			},
		
		messages: {
		      docName:
		        {
			    	  require_from_group:"",
			    	  allowSpecialChar:"<fmt:message key='REQUIRED_VALID_NAME'/>",
					maxlength: "<fmt:message key='INPUT_50_CHAR_NEW'/>"
					},
				doctype_city:
				{
					require_from_group:"",
					typeHeadDropDown: "! Please select a valid document type"
				},
				shared:
				{
					require_from_group:""
				},
				linked:
				{
					require_from_group:""
				},
				sharedWith:
				{
					require_from_group:"",
					autoCompleteOption: "Organization does not exist. Please enter an existing Provider or Agency"
				},
				submittedfrom: {
					require_from_group:"",
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
				}, 
				submittedfrom1: {
					require_from_group:"",
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past",
					required:"This field is required"
					}, 
				submittedTo: {
					require_from_group:"",
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					DateToFrom : "<fmt:message key='REQUIRED_VALID_DATE1'/>",
					calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
					},
				submittedTo1: {
					require_from_group:"",
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					DateToFrom : "<fmt:message key='REQUIRED_VALID_DATE1'/>",
					calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
						},
				procurementTitle:{
					autoCompleteOption: "! Procurement does not exist. Please enter an existing Procurement Title",
					required:"This field is required"
				},
				amendmentepinTitle:{
					autoCompleteOption: "! Invalid E-PIN. Please enter a valid E-PIN.",
					required:"This field is required"
				},
				awardepinTitle:{
					autoCompleteOption: "! Invalid E-PIN. Please enter a valid E-PIN.",
					required:"This field is required"
				},
				contractawardepinTitle:{
					autoCompleteOption: "! Invalid E-PIN. Please enter a valid E-PIN.",
					required:"This field is required"
				},
				invoiceNum:{
					autoCompleteOption: "! Invalid Invoice Number. Please enter a valid Invoice Number.",
					required:"This field is required"
				}
		},
		submitHandler: function(form){
			$('#messagediv').hide();
			searchFormSubmit(form);
		},
		invalidHandler: function(event, validator) {
			for(var i in validator.errorMap)
				{
					if(validator.errorMap[i].length>0)
						{
							$(".messagediv").hide();
						}
					else{
						var _message= "You must select at least 1 search criteria.";
						$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
				  		$(".messagediv").addClass("failed");
				  		$(".messagediv").show();
					}
				}
			
		},
		errorPlacement: function(error, element) {
			if(element.parent().parent().attr('class')== 'formfield modifieddaterange'){
				$(error).append('&nbsp;');
				error.appendTo(element.parent().parent().find("span.error[for='"+$(element).attr("id")+"']"));
			}
			else{
		      error.appendTo(element.parent().parent().find("span.error"));
			}
		}
		});
	//end
	//Form for find organization documents search
	$("#findorgdocform").validate({
		rules: {
			docName: {
				
				allowSpecialChar: ["A","_,.\\\"\\\'\\\ -"],
				maxlength: 50
				},
				 doctype_city: {
					 typeHeadDropDown:true,
				required: true
				
			},	
			 modifiedfrom5: {
				minlength : 10,
				maxlength : 10,
				 DateFormat : true,
				 calenderRestrictFutureDate : true,
				DateRange : new Array("01/01/1800",today) 
			},
			 modifiedto5: {
				minlength : 10,
				maxlength : 10,
				 DateFormat : true, 
				 calenderRestrictFutureDate : true,
				 DateRange : new Array("01/01/1800",today),
				 DateToFrom: new Array("modifiedfrom5",false,"=") 
			}
		},
		
		messages: {
			docName:
		        	{
		    	 allowSpecialChar:"<fmt:message key='REQUIRED_VALID_NAME'/>",
				maxlength: "Document Name cannot exceed 50 characters"
					},
			doctype_city:
		        	{
				required: "<fmt:message key='REQUIRED_FIELDS'/>",
				typeHeadDropDown:"! Please select a valid document type"
					},
		    modifiedfrom5: {
				minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				 DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
				calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
				}, 
			modifiedto5: {
				minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				 DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
				DateToFrom : "<fmt:message key='REQUIRED_VALID_DATE1'/>"  ,
				calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
					}
				
		},
		submitHandler: function(form){
			document.findorgdocform.action = findOrgDocForm+"&submit_action=filterdocuments&isFilter=true&sharedFlag=false&jspName=sharedSearchAgency";
			$(document.findorgdocform).ajaxSubmit(option1);
			pageGreyOut();
			$("#findorgdocform #findDoc").hide();
		},
		errorPlacement: function(error, element) {
			if(element.parent().parent().attr('class')== 'formfield modifieddaterange'){
				error.appendTo(element.parent().parent().find("span.error[for='"+$(element).attr("id")+"']"));
			}
			else{
		      error.appendTo(element.parent().find("span.error"));
			}
		}
		});
	//end
	////Form for recycle bin search
	$("#deleteform").validate({
		rules: {
			docName: {
				require_from_group: [1,".groupName"],
				allowSpecialChar: ["A","_,.\\\"\\\'\\\ -"],
				maxlength: 50
				},
			docTypeRec: {
				require_from_group: [1,".groupName"],
				typeHeadDropDown: true
				},
			modifiedfrom2: {
				require_from_group: [1,".groupName"],
				minlength : 10,
				maxlength : 10,
				DateFormat : true,
				calenderRestrictFutureDate : true,
				DateRange : new Array("01/01/1800", today)
			},
			
			modifiedto2: {
				require_from_group: [1,".groupName"],
				minlength : 10,
				maxlength : 10,
				DateFormat : true,
				calenderRestrictFutureDate : true,
				DateRange : new Array("01/01/1800", today),
				DateToFrom: new Array("modifiedfrom2",false,"=")
			}
			
			},
		
			messages: {
			      docName:
			        	{
			    	require_from_group:"",
			    	allowSpecialChar:"<fmt:message key='REQUIRED_VALID_NAME'/>",
					maxlength: "<fmt:message key='INPUT_50_CHAR_NEW'/>"
						},
					docTypeRec:{
						require_from_group:"",
						typeHeadDropDown: "! Please select a valid document type"
					},	
					modifiedfrom2: {
						require_from_group:"",
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
					}, 
					
					modifiedto2: {
						require_from_group:"",
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					DateToFrom: "<fmt:message key='REQUIRED_VALID_DATE1'/>",
					calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
						}
						
					
			},
			submitHandler: function(form){
				//Adhoc - ManageOrg Recycle Bin search was not working ...Adding below if-else code in Emergency Release 4.0.2
				var orgType;
				var orgId;
				if($js("#selectOrgnization").jstree(true))
					{
					var selectOrgnizationTreeSelected = $js("#selectOrgnization").jstree("get_selected");
					if(selectOrgnizationTreeSelected.length > 0)
						{
						orgType = $js("#selectOrgnization").jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationType;
						orgId = $js("#selectOrgnization").jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationId;
						}
					}
				else
					{
					var selectOrgnizationTreeSelected = $js("#leftTree").jstree("get_selected");
					if(selectOrgnizationTreeSelected.length > 0)
						{
						orgType = $js("#leftTree").jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationType;
						orgId = $js("#leftTree").jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationId;
						}
					}
				
				$('#messagediv').hide();
				// Adding two parameters for Emergency Release 4.0.2
				document.deleteform.action = deletesearchform+"&action=documentVault&sharedFlag=false&submit_action=filterdocuments&isFilter=true&folderId=RecycleBin&jspName=recyclebin&normalSearchOrgType="+orgType
				+"&normalSearchOrgId="+orgId;
				$(document.deleteform).ajaxSubmit(option1);
				pageGreyOut();
				$("#recyclebindiv").slideToggle();
			},
			invalidHandler: function(event, validator) {
				for(var i in validator.errorMap)
					{
						if(validator.errorMap[i].length>0)
							{
								$(".messagediv").hide();
							}
						else{
							var _message= "You must select at least 1 search criteria.";
							$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
					  		$(".messagediv").addClass("failed");
					  		$(".messagediv").show();
						}
					}
				
			},
			errorPlacement: function(error, element) {
				if(element.parent().parent().attr('class')== 'formfield modifieddaterange'){
					error.appendTo(element.parent().parent().find("span.error[for='"+$(element).attr("id")+"']"));
				}
				else{
			    error.appendTo(element.parent().parent().find("span.error"));
				}
			}
		});
		//end
		//variables for ajax submit
	var option1 = {
		success : function(responseText, statusText, xhr)
		{
			var response = new String(responseText);
			var responses = response.split("|");
			if (!(responses[1] == "Error" || responses[1] == "Exception")) {
				removePageGreyOut();
				$(".messagediv").hide(); 
				var data = $(responseText);
				if (data.find(".tabularWrapper").size() > 0) {
					$("#myform .tabularWrapper").html(
							data.find(".tabularWrapper").html());
					if($("#hideEmptyRecycleBin").size() > 0 && parseInt($("#hideEmptyRecycleBin").val()) == 0){
						$("#downloadAll").hide();
					}else if($("#hideEmptyRecycleBin").size() > 0 && parseInt($("#hideEmptyRecycleBin").val()) > 0){
						$("#downloadAll").show();
					} 
					// Adding below if for Download All hide on Recycle Bin Search  Emergency Relase 4.0.2
					if(!$("#findOrgDoc").hasClass("selected"))
						{
						$("#downloadAll").hide();
						}
					$(".overlay").closeOverlay();
					removePageGreyOut();
				}
			} else {
				$(".overlay").closeOverlay();
				var _message= "You must select at least 1 search criteria.";
				$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		  		$(".messagediv").addClass("failed");
		  		$(".messagediv").show();
				removePageGreyOut();
			}
		}
	};
	
	var options = 
	{
		success: function(responseText, statusText, xhr ) 
		{
		var responseString = new String(responseText);
		var responsesArr = responseString.split("|");
		if(!(responsesArr[1] == "Error" || responsesArr[1] == "Exception"))
		{
	        removePageGreyOut();
	        var data = $(responseText);
	        if(data.find(".tabularWrapper").size()>0)
	   		{
	        	 $("#documentVaultGrid.tabularWrapper").replaceWith(data.find(".tabularWrapper"));
	        	 var _cusFolderId = $('#selectedfolderid').val();
		 	        var _folderName = $('#folderName').val();
		 	        var _newFolderId = data.find("#newFolderId").val();
	 	        var _message = data.find("#message").val();
	 	        $(".messagediv").removeClass("failed passed");
	 	        var _messageType = data.find("#messageType").val();
	 	      if(_newFolderId != "null" && null != _newFolderId && _newFolderId != "" && _newFolderId.length > 0)
	 	    	  {
	 	    	refreshLeftTree(_cusFolderId);
	 	    	  }
	 	        $(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
	 			$(".messagediv").addClass(_messageType);
	 			$(".messagediv").show();
	 	        $(".overlay").closeOverlay();
	 	       setTimeout(function() {removePageGreyOut()},1500);
	   		}
	        else
	        {
	        	
	        	 var _message = data.find("#message").val();
	 	        $(".messagediv").removeClass("failed passed");
	 	        var _messageType = data.find("#messageType").val();
	 	        $(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
	  			$(".messagediv").addClass(_messageType);
	  			$(".messagediv").show();
	        	$(".overlay").closeOverlay();
 	        removePageGreyOut();
 	      
	        	
	        }
	       
		}
		else
			{
			// Added for defect 7560
			//defect 7982 indexOf added
			if(responsesArr[3].indexOf("The selected destination folder has a current transaction in progress.")>-1)
			{
				$(".messagediv").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
				$(".messagediv").addClass(responsesArr[4]);
		        $(".messagediv").show();
		        $(".overlay").closeOverlay();
			}
			// Added for defect 7560
			else{
			$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
            $(".messagedivover").addClass(responsesArr[4]);
            $(".messagedivover").show();
			}
            removePageGreyOut();
			}
			
		},
		error:function (xhr, ajaxOptions, thrownError)
		{                     
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	//end
	//form for new folder overlay
	$("#newfolder").click(function(){
		$("#folderName").val("");
	    $("#folderName").blur(function(){
	        $(this).css({'border':''});
	    });
		$(this).closest('form').find("input[type=text], textarea").val("");
		$js('#newFolderTree').jstree("destroy");
		$('.messagedivover').empty();
		$('.messagedivover').hide();
		$('.error').empty();
		
		$(".overlay").launchOverlay($(".alert-box-newFolder"), $(".exit-panel"), "400px", "auto", null);
			tree($('#hdnopenTreeAjaxVar').val(), 'newFolderTree','selectedfolderid', $js("#leftTree").jstree("get_selected"),'');
		});
		//end	
	});
	//end of ready function
	//this function is executed when edit information button is clicked
function editDocInfo(documentId){
	$('.radioButtonHelp').attr('disabled',false);
	$('#editDocName').hide();
	$('#editDocNameText').show();
	$('.imgClass #cal').show();
	$('.customProp').show();
	$('.customPropformfield').hide();
	$('#editViewCancelButtonDoc').show();
	$('#editViewSaveButtonDoc').show();
	$('#editDocInfo').hide();
	$('#periodcoveredfrom,#periodcoveredto,#effectivedate').siblings('span.imgClass').show();
	$('#editDocNameText').val($('#editDocName').text());
	$('#helpcategory').val($('#helpcategory').siblings('span.formfield').text())
	$('#helpdesc').val($('#helpdesc').siblings('span.formfield').text())
	$('#periodcoveredto').val($('#periodcoveredto').siblings('span.formfield').text())
	$('#periodcoveredfrom').val($('#periodcoveredfrom').siblings('span.formfield').text())
	$('#samplecategory').val($('#samplecategory').siblings('span.formfield').text())
	$('#sampletype').val($('#sampletype').siblings('span.formfield').text())
	$('#effectivedate').val( $('#effectivedate').siblings('span.formfield').text())
	$('#meetingdate').val( $('#meetingdate').siblings('span.formfield').text())
	var optionsDocForm = 
			{
				success: function(responseText, statusText, xhr ) 
				{ 
					var responseString = new String(responseText);
					var responsesArr = responseString.split("|");
					if(!(responsesArr[1] == "Error" || responsesArr[1] == "Exception"))
					{
				        var data = $(responseText);
				        // Adding Id documentVaultGrid for Emergency Release 4.0.1
				         $("#documentVaultGrid.tabularWrapper").replaceWith(data.find(".tabularWrapper"));
				       	 $('#editViewCancelButtonDoc,#editViewSaveButtonDoc,#editDocNameText').hide();
				       	 $('#periodcoveredto,#periodcoveredfrom,#helpcategory,#helpdesc,#meetingdate').hide();
				         $('#periodcoveredfrom,#periodcoveredto,#effectivedate,#meetingdate').siblings('span.imgClass').hide();
				       	 $('#samplecategory,#sampletype,#effectivedate').hide();
				       	
				       	 $('#editDocName').show(); 
				     	 //help category fields
				       	$('#helpcategory').siblings('span.formfield').show();
				       	$('#helpdesc').siblings('span.formfield').show();
				       	$('.radioButtonHelp').attr('disabled',true);
				       	$('#meetingdate').siblings('span.formfield').show();
				       	
				       	$('#periodcoveredto').siblings('span.formfield').show();
				        $('#periodcoveredfrom').siblings('span.formfield').show();
				        $('#samplecategory').siblings('span.formfield').show();
				        $('#sampletype').siblings('span.formfield').show();
				        $('#effectivedate').siblings('span.formfield').show();
				        $('#helpcategory').siblings('span.formfield').show();
				 	        removePageGreyOut();
				 	       $('a[title*="Edit Properties"]').show();
						var _newValue=$('#editDocNameText').val();
						$('#editDocName').text(_newValue);
						//help document
						$('#helpcategory').siblings('span.formfield').text($('#helpcategory').val());
						$('#helpdesc').siblings('span.formfield').text($('#helpdesc').val());
						
						$('#meetingdate').siblings('span.formfield').text($('#meetingdate').val());
						
						$('#periodcoveredto').siblings('span.formfield').text($('#periodcoveredto').val());
				        $('#periodcoveredfrom').siblings('span.formfield').text($('#periodcoveredfrom').val());
				        $('#samplecategory').siblings('span.formfield').text($('#samplecategory').val());
				        $('#sampletype').siblings('span.formfield').text($('#sampletype').val());
				        $('#effectivedate').siblings('span.formfield').text($('#effectivedate').val());
				        //Doctype value- updating in case of sample doctype
				        if($('#sampletype').size()==1){
				        var lsSampleCategory = "Sample Document"
						var lsSelectedSampleType=$('#sampletype option:selected').val();
						$('#docTypeInfo span.formfield').text(lsSampleCategory+" - "+lsSelectedSampleType);
				        }
				        //Doctype value end
					}
					else
					{
						$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivoverCs', this)\" />");
			            $(".messagedivover").addClass(responsesArr[4]);
			            $(".messagedivover").show();
			            removePageGreyOut();
					}
					
						
					
				},
				error:function (xhr, ajaxOptions, thrownError)
				{                     
					showErrorMessagePopup();
					removePageGreyOut();
				}
			};
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; //January is 0!

	var yyyy = today.getFullYear();
	if(dd<10){
	    dd='0'+dd;
	} 
	if(mm<10){
	    mm='0'+mm;
	} 
	var today = mm +'/' +  dd+'/' +yyyy; 
	//form for document save after editing		
	$("#docSaveForm").validate({
		rules: {
		editDocNameText:{
			required: true,
			allowSpecialChar: ["A"," _-"],
			maxlength: 50,
			ValidateName:true
		},
		helpcategory:{
			required: true
		},
		meetingdate:{
			required: true,
			minlength : 10,
			maxlength : 10,
			DateFormat : true,
			calenderRestrictFutureDate : true,
			DateRange : new Array("01/01/1800", today)
		},
		effectivedate:{
			required: true,
			minlength : 10,
			maxlength : 10,
			DateFormat : true,
			calenderRestrictFutureDate : true,
			DateRange : new Array("01/01/1800", today)
		},
		periodcoveredfrom:{
			required: true,
			minlength : 10,
			maxlength : 10,
			DateFormat : true,
			calenderRestrictFutureDate : true,
			DateRange : new Array("01/01/1800", today)
		},
		periodcoveredto:{
			required: true,
			minlength : 10,
			maxlength : 10,
			DateFormat : true,
			calenderRestrictFutureDate : true,
			DateRange : new Array("01/01/1800", today),
		    DateToFrom: new Array("periodcoveredfrom",false)
		}
		},
		messages: {
			editDocNameText: {
				allowSpecialChar:"<fmt:message key='ALPHANUMERIC_ALLOWED_DOCUMENT_NAME'/>",
				maxlength: "<fmt:message key='INPUT_50_CHAR'/>",
				required: "<fmt:message key='REQUIRED_FIELDS'/>",
				ValidateName:"Using This Folder Name Is Restricted"
				},
				helpcategory:{
					required: "<fmt:message key='REQUIRED_FIELDS'/>"
				},
				meetingdate: {
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					required: "<fmt:message key='REQUIRED_FIELDS'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
					},
				effectivedate: {
					minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
					required: "<fmt:message key='REQUIRED_FIELDS'/>",
					DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
					calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
					},
				periodcoveredfrom: {
				minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				required: "<fmt:message key='REQUIRED_FIELDS'/>",
				DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
				calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
				},
				periodcoveredto: {
				required: "<fmt:message key='REQUIRED_FIELDS'/>",
				minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
				DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
				DateToFrom : "<fmt:message key='REQUIRED_VALID_DATE1'/>",
				calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
				}
		},
		submitHandler: function(form){
			document.docSaveForm.action = document.docSaveForm.action+"&submit_action=saveProperties&documentId="+documentId;
			pageGreyOut();
			$(document.docSaveForm).ajaxSubmit(optionsDocForm);
			
		},
		errorPlacement: function(error, element) {
			if(element.parent().attr('class')== 'row periodCoveredClass'){
				error.appendTo(element.parent().find("span.error"));
			}
			else{
		      error.appendTo(element.parent().find("span.error"));
			}
		}
	});	
	//end
//Release 5 starts: added dropdown for sample category and its type during edit info functionality
$('#samplecategory').change(function() {
	if(samplefilterCategoryForCity()){
	getFilterSampleTypeForCity();
	}	
});
//This will execute when any option is selected for filter Document Category
function samplefilterCategoryForCity() {
	var e = document.getElementById('samplecategory');
	var category = e.options[e.selectedIndex].value;
	if (category == null || category == "") {
		document.getElementById("sampletype").value = "";
		document.getElementById("sampletype").disabled = true;
		return false;
	} else {
		document.getElementById("sampletype").disabled = false;
		return true;
	}

}
//will return the list of document types through servlet call
function getFilterSampleTypeForCity() {
	pageGreyOut();
	var selectedInput = document.getElementById("samplecategory").value;
	var url = $("#contextPathSession").val()+"/GetContent.jsp?&selectedInput=" + selectedInput+"&organizationId=<%=ApplicationConstants.PROVIDER_ORG%>";
	postRequest(url);
	removePageGreyOut();
}
//This will execute when any servlet call is made
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

//This will execute to get filter document type for filter document category
function updatepage(str) {
	var n = str.split("|");
	var selectbox = document.getElementById("sampletype");
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
//Release 5 end: added dropdown for sample category and its type during edit info functionality
}
	//end
//this function is executed when edit information button is clicked
function editFolderInfo(documentId){
	$('#editFolderName,#editFolderInfo').hide();
	$('#editFolderNameText,#editViewCancelButton,#editViewSaveButton').show();
	$("#editFolderNameText").val($('#editFolderName').text());
	var optionsFolderSaveForm = 
			{
				success: function(responseText, statusText, xhr ) 
				{ 
					var responseString = new String(responseText);
					var responsesArr = responseString.split("|");
					if(!(responsesArr[1] == "Error" || responsesArr[1] == "Exception" || responsesArr[1] == "CloseOverlay") )
					{
				        var data = $(responseText);
				        	 $("#documentVaultGrid.tabularWrapper").replaceWith(data.find(".tabularWrapper"));
				        	 var _updatedFolderName = data.find("#updatedFolderName").val();
				 	    $('#editViewSaveButton,#editViewCancelButton,#editFolderNameText').hide();
						$('#editFolderName').text($("#editFolderNameText").val());
				 	    $('#editFolderName').show();
				 	   refreshLeftTree($js('#leftTree').jstree("get_selected"));
				 	      	sortTreeNode($("#selectedfolderid").val());
				 	        removePageGreyOut();
				 	       $('#messagedivoveredit').hide();
				 	       $('a[title*="Edit Properties"]').show();
					}
					else
					{
						// added for 7666
						if(responsesArr[1] == "CloseOverlay"){
							$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivoveredit', this)\" />");
							$(".messagedivover").addClass(responsesArr[4]);
					        $(".messagedivover").show();
					        removePageGreyOut();
						}
						// added for 7666
						else{
						var responsesArr = responseString.split("|");
						$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivoveredit', this)\" />");
		            $(".messagedivover").addClass(responsesArr[4]);
		            $(".messagedivover").show();
						}
		            removePageGreyOut();
					}
				},
				error:function (xhr, ajaxOptions, thrownError)
				{                     
					showErrorMessagePopup();
					removePageGreyOut();
				}
			};
	//form for folder save after editing	
	$("#folderSaveForm").validate({
		rules: {
			editFolderNameText: {
				required: true,
				allowSpecialChar: ["A"," _-"],
				maxlength: 50,
				ValidateName:true}

		},
		messages: {
			editFolderNameText: {
				required: "<fmt:message key='REQUIRED_FIELDS'/>",
				allowSpecialChar:"<fmt:message key='ALPHANUMERIC_ALLOWED_DOCUMENT_NAME'/>",
				maxlength: "Folder Name cannot exceed 50 characters",
				ValidateName:"Using This Folder Name Is Restricted"
				}
		},
		submitHandler: function(form){
			document.folderSaveForm.action = document.folderSaveForm.action+"&submit_action=saveProperties&documentId="+documentId+"&lsRenderAction=No" ;
			pageGreyOut();
			$(document.folderSaveForm).ajaxSubmit(optionsFolderSaveForm);
			
		},
		errorPlacement: function(error, element) {
		      error.appendTo(element.parent().parent().find("span.error"));
		}
	});	
	//end
	}
//end
</script>

<!-- End -->
<!-- Jsp Body Starts -->
<c:if test="${not empty portletSessionScope.providerNameForSharedDoc}">
<h2><%=StringEscapeUtils.unescapeJavaScript((String)ApplicationSession.getAttribute(renderRequest, true, "providerNameForSharedDoc"))%></h2>
			<div class="linkReturnVault">
				<a title='Return to Home' href="<portlet:actionURL ><portlet:param name="next_action" value="returnToHome" /></portlet:actionURL>">Return to Home</a>
			</div>
			</c:if>
<h2 class="h2Class">Document Vault</h2>
<c:if test="${org_type ne 'city_org' and ForManageOrganization ne 'manageOrg'}">
	<div class="iconQuestion-alignment iconQuestion"><a href="javascript:void(0);"
		title="Need Help?" onclick="pageSpecificHelp('Document Vault');"></a></div>
</c:if>
<div>

<!--Start  QC 9633 R 9.1   text for Document Vault -->
<!-- Manage documents uploaded by your organization.   -->
To access your organization’s <b>CHAR410, CHAR500+990+audit, or CHAR500 Notice of Exemption</b> documents in the HHS Accelerator Document Vault, search the corresponding document type in the "Document Name" search field. Users can also access these documents by sorting by "Document Type", "Name" or "Modified Date". These documents will not be available for selection under the "Document Type" drop-down.
<%-- <c:choose>
    <c:when test="${org_type ne 'city_org'}" >
       To access your organization’s <b>CHAR410, CHAR500+990+audit,</b> or <b>CHAR500 Notice of Exemption</b> documents in the HHS Accelerator Document Vault, search the corresponding document type in the "Document Name" search field. Users can also access these documents by sorting by "Document Type", "Name" or "Modified Date". These documents will not be available for selection under the "Document Type" drop-down.
    </c:when>    
    <c:otherwise>
       Manage documents uploaded by your organization.
    </c:otherwise>
</c:choose> --%>
<!--End  QC9633 R 9.1   text for Document Vault -->

<c:if test="${org_type eq 'provider_org' && !headerClick}" >
<form method="post" style='display:inline;' action="<portlet:actionURL ><portlet:param name="action" value="providerAgencyHome" /></portlet:actionURL>" name="providerInfo">
			</form>
			<select id = "providerSharedOrgId" name="providerSharedOrgId" class="input" >
				<option selected="selected">Switch to a different organization</option>
				<c:forEach var="organization" items="${portletSessionScope.sharedDocForProvider}">
					<option value="<c:out value="${organization.key}"/>"><c:out value="${organization.value}"/></option>
				</c:forEach>
			</select>
</c:if>
</div><br>
<div class="messagediv" id="messagediv"></div>
<c:if test="${not empty messageType && not empty message}">
		<div class="${messageType}" id="messagediv" style="display:block">${message} <img
			src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
			class="message-close" onclick="showMe('messagediv', this)">
		</div>
</c:if>

<!-- Document Vault Header -->
<c:set var="check"><%= session.getAttribute("permissionType") %></c:set>
<!--start QC 8914 R7.2 read only role  --> 
<%if( CommonUtil.hideForOversightRole(request.getSession()) ){%>
		<c:set var="check" value="R"></c:set>	
<%}%> 
<c:set var="role_current"><%= session.getAttribute("role_current") %></c:set>
<!--end QC 8914 R7.2 read only role  -->
<div class="vaultheader hideShow" id="vaultheader" data-label="MenuHeader">
        <!--/*--   [Start] QC9744      -*/  -->
       <%  String actionMenu = "true"; %>
        <!--/*--   [End] QC9744      -*/  -->
<c:choose>
    <c:when test="${org_type eq 'city_org'}">
    	<%actionMenu = CommonUtil.getDocVaultActionCityAvailable(); %>
    </c:when>    
	 <c:when test="${org_type eq 'agency_org'}">
		<%actionMenu = CommonUtil.getDocVaultActionCityAvailable(); %>
    </c:when>    
	 <c:when test="${org_type eq 'provider_org'}">
		<%actionMenu = CommonUtil.getDocVaultActionProviderAvailable(); %>
    </c:when>    
    <c:otherwise>
		<% actionMenu = "true"; %>
    </c:otherwise>
</c:choose>



<c:if test="${check ne 'R'}">
	<c:if test="${(ForManageOrganization ne 'manageOrg') }">
		<span class="leftfloatelem">
		<!--/*--   [Start] QC9744      */  --> 
		<% if( actionMenu != null && actionMenu.equalsIgnoreCase("true")   ) {%>  <!--/*--   [End] QC9744      -*/  -->
	  		<span id="newfolder" class="hideShow plus-icon plus-icon-align"><span class="foldercolor" title="Create a new folder">New Folder</span></span>
	  		<span id="uploadDoc" class="hideShow upload-icon upload-icon-align"><span class="foldercolor" title="Upload a new document">Upload Document</span></span>
	  		
	  		<span class="hideShow emptyBin emptyBin-align" style="display:none;" id="emptyBin" onclick="emptyRecycleBinOverlay()" ><span class="foldercolor" title="Permanently delete all items in your organization’s recycle bin">Empty Bin</span></span>
        <!--/*--   [Start] QC9744      */  --> 
        <%} %>    <!--/*--   [End] QC9744  -*/  -->	  		
		</span>
	</c:if>
</c:if>
    <span class="rightfloatelem">
    <c:if test="${check ne 'R'}">
       <c:if test="${(ForManageOrganization ne 'manageOrg') }">
      	<span class="file_menu file_menu-align" id="file_menu">
      </c:if>
      <c:if test="${(ForManageOrganization ne 'manageOrg') }">
      	<span class="foldercolor" title="Options for selected folder or documents">File Options</span>
      </c:if>
       <!-- Chat Box  -->
        <div class=" fileoptions1 fileoptions1-hoveralignment-main chatbox-enlarge fileoptions1-align" style='display:none;'>
       
        <!-- added fileoptions2 css in release 4.0.1.0 to set chat box width  -->
        <div class="chatbox-content fileoptions2" id='chatbox-content1'>
	       	 <ul>
	           <li><a href ='#' onclick='move("", "")'><img src='../framework/skins/hhsa/images/Black_Move_icon.png' alt='someimage'/><p>Move</p></a></li>
	            <% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S030_SECTION, request.getSession()) || 
	       			CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())||
	       			CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S032_CITY_PAGE, request.getSession())){%> 
	        	<li><a class="anchoralign" href='#' onclick='shareEntityInfo("", "","")'><img src='../framework/skins/hhsa/images/share_icon.png' alt='someimage'/><p>Share</p></a></li>
	              <li><a href='#' onclick='unShareEntity()'><img src='../framework/skins/hhsa/images/unshare_icon.png' alt='someimage'/><p>UnShare</p></a></li>
	              <%}%> 
	             <hr align="center" width=80%>
	              <li><a href="javascript:" onclick="javascript: deleteData('','null','null')"><img src="../framework/skins/hhsa/images/delete_icon.png" alt="someimage"/><p>Delete</p></a></li>

	          </ul>
      </div>
      <!-- added fileoptions2 css in release 4.0.1.0 to set chat box width  -->
     <div class="chatbox-content fileoptions2" id='chatbox-content2'>
         <ul>
           <li><a href ='#' onclick='move("", "")'><img src='../framework/skins/hhsa/images/Black_Move_icon.png' alt='someimage'/><p>Move</p></a></li>

            <% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S030_SECTION, request.getSession()) || 
	       			CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())||
	       			CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S032_CITY_PAGE, request.getSession())){%> 
            <li><a class="anchoralign" href='#' onclick='shareEntityInfo("", "","")'><img src='../framework/skins/hhsa/images/share_icon.png' alt='someimage'/><p>Share</p></a></li>
             <%}%>
           <hr align="center" width=80%> 
         <li><a href="javascript:" onclick="javascript: deleteData('','null','null')"><img src="../framework/skins/hhsa/images/delete_icon.png" alt="someimage"/><p>Delete</p></a></li>
        </ul>
      </div>
       <!-- added fileoptions2 css in release 4.0.1.0 to set chat box width  -->
      <div class="chatbox-content fileoptions2" id='chatbox-content3'>
         <ul>
           <li><a href ='#' onclick='move("", "")'><img src='../framework/skins/hhsa/images/Black_Move_icon.png' alt='someimage'/><p>Move</p></a></li>
           <hr align="center" width=80%> 
           <li><a href="javascript:" onclick="javascript: deleteData('','null','null')"><img src="../framework/skins/hhsa/images/delete_icon.png" alt="someimage"/><p>Delete</p></a></li>
         
        </ul>
      </div>
      
      <c:if test="${check ne 'R'}">
       <!-- added fileoptions3 css in release 4.0.1.0 to set chat box width  -->
     <div class="chatbox-content fileoptions3" id='chatbox-content-recyclebin-header'>
       <ul>
        <li><a href='#' onclick='restore("","")'><img src='../framework/skins/hhsa/images/Restore_icon.PNG' alt='someimage'/><p>Restore</p></a></li>
        <li><a href='#' onclick='deleteForever("","")'><img src='../framework/skins/hhsa/images/Delete_forever.PNG' alt='someimage'/><p>Delete Forever</p></a></li>
       </ul>
     </div>
     </c:if>
    </div>
     <c:if test="${ForManageOrganization ne 'manageOrg'}">
      </span>
     </c:if>  
     </c:if>
  	<span class="searchDocument searchDocument-align" id="searchDocument" onclick="searchDoc()">
 	<c:choose>
 	
 	<c:when test="${ForManageOrganization eq 'manageOrg' && org_type eq 'city_org'}">
 	<span id="searchicon" class="foldercolor" title="Search for specific documents from this organization">Search</span>
 	</c:when>
 	<c:when test="${ForManageOrganization eq 'manageOrg' && (org_type eq 'provider_org' || org_type eq 'agency_org')}">
 	<span id="searchicon" class="foldercolor" title="Search for specific shared documents from this organization">Search</span>
 	</c:when>
 	<c:otherwise>
 	<span id="searchicon" class="foldercolor" title="Search for specific documents in your vault">Search</span>
 	</c:otherwise>
 	</c:choose>
 	</span>
	</span>
	 
	 
	 
	
</div>
<div class="vaultheader" id="findOrgDocbtn" data-label="MenuHeader" style="display:none;">
<span class="leftfloatelem">
 <span id="OrgNameHeader" style="display:none;margin-left:18px;font-size: 15px !important;font-weight:bold;" class="foldercolor"></span>
</span>
  
 <span class="rightfloatelem">
 <span class="downloadDocument searchDocument-align" id="downloadAll"><span class="foldercolor downloadDocument-align" title="Start the process of downloading all the documents">Download All</span></span>
 <span id="removeList" title="Remove this organization from the shortcuts list in the left side navigation" class="removeList foldercolor">X Close</span>
 <c:if test="${org_type eq 'city_org'}">
  <span class="searchDocument searchDocument-align searchdocbutton" id="searchdocbutton"><span class="foldercolor" title="Search for specific documents across all organizations in the system.">Search</span></span>
 </c:if>
 <c:if test="${org_type eq 'agency_org'}">
  <span class="searchDocument searchDocument-align searchdocbutton" id="searchdocbutton"><span class="foldercolor" title="Search for specific shared documents across all organizations that have shared with you">Search</span></span>
 </c:if>
 </span>
</div>

<!-- End -->
<jsp:include page="search.jsp"></jsp:include>
<form id="myform" action="<portlet:actionURL/>" method ="post" name='provform'>
<!-- Hidden Parameters -->
<c:if test="${not empty portletSessionScope.SharedOrgSessionList}">
<input type="hidden" id="orgPresent" value="true"/>
</c:if>

<c:if test="${ForManageOrganization eq 'manageOrg'}">
<input type="hidden" id="manageOrganization" value="true"/>
</c:if>

<input type="hidden" id="removeSession" value="${removeSession}"/>
<input type="hidden" id="readOnlyCheck" name = "readOnlyCheck" value="<%= session.getAttribute("permissionType") %>"/>

<input type="hidden" id="role_current" name = "role_current" value="<%= session.getAttribute("role_current") %>"/>


	<input type="hidden" id="menuFlag" name="menuFlag" value=""/>
	<input type="hidden" id="entity_type" value=""/>
	<input type="hidden" id="sharedPageOrg" value=""/>
	<input type="hidden" id="viewProposal" value="${editProposal}"/>
	<input type="hidden" id="manageTreeOrgId" value="${ManageTreeOrgId}"/>
	<input type="hidden" id="ManageTreeOrgType" value="${ManageTreeOrgType}"/>
	<input type="hidden" id="findSharedDocsUrl" value="${findSharedDocs}"/>
	<input type="hidden" id="checkLinkageParam" value="${checkLinkage}"/>
	<input type="hidden" id="hdnopenTreeAjaxVar" value="${openTreeAjax}"/>
	<input type="hidden" id="hdnopenTreeAjaxVarOrg" value="${openTreeAjaxOrg}"/>
	<input type="hidden" name = "checkedDocumentId" id="checkedDocumentId" value=""/>
	<input type="hidden" name = "checkedDocumentType" id="checkedDocumentType" value=""/>
	<input type="hidden" id="checkLocking" value="${checkLocking}"/>
	<input type="hidden" name="next_action" value="" id="next_action"/>
	<input type="hidden" id="linkage" value="${linkage}"/>
	<input type="hidden" id="agencySet" value="${agencySet}"/>
	<input type="hidden" id="providerSet" value="${providerSet}"/>
	<input type="hidden" id="filterStatus" value="${filterStatus}"/>
	<input type="hidden" id="message" value="${message}"/>
	<input type="hidden" name="messageType" id ="messageType" value="${messageType}" />
	<input type="hidden" name="presentFolderId" id ="presentFolderId" value='' />
	<input type="hidden" name="immediateParent" id ="immediateParent" value='' />
	<input type="hidden" name="childToMove" id ="childToMove" value='' />
	<input type="hidden" name="flag" id="flag" value='' />
	<input type="hidden" name="selectedfolderidformove" id ="selectedfolderidformove" value='${SelectedFolderId}' />	
	<input type="hidden" name="homePageManageOrgFlag" id ="homePageManageOrgFlag" value='${homePageManageOrgFlag}' />	
	<input type="hidden" name="docTypeHidden" id="docTypeHidden" value='' />
	<input type="hidden" name="docCatHidden" id="docCatHidden" value='' />
	<input type="hidden" name="deletedId" id="deletedId" value='' />
	<input type="hidden" name="docTypeForRecycle" value="" id="docTypeForRecycle"/>
	<input type="hidden" name="contextPathVal" id="contextPathVal" value='${pageContext.servletContext.contextPath}' />
	
	<input type="hidden" name="sharedWithValue" value="" id="sharedWithValue"/>
	<input type="hidden" name="lockingForViewhidden" value="" id="lockingForViewhidden"/>
	
	
	
	<!-- End -->
<div class='hr'></div>
<div class='clear'></div>
<!-- End -->

<!-- Form Data Starts -->
<div id="mymain">
	<div class="" style="height:500px;">
		<div class="leftTreeStructure">
			<div class="leftTreeWrapper1">
				<div class="leftTreeScroll1"></div>
			</div>
			<div class="leftTree leftTreeWrapper2">
				<input type="hidden" name="RecyclebinFlag" id="RecyclebinFlag" value='' />
				<input type="hidden" name="RecyclebinId" id="RecyclebinId" value='' />
				<div id="leftTree" class="leftTreeScroll2"></div>
				<c:if test="${ForManageOrganization ne 'manageOrg'}">
					<c:if test="${org_type ne 'provider_org'}">
						<span>
							 <ul>
								<hr width="90%">
								<c:if test="${org_type eq 'city_org'}">
									<li class="findOrgDoc findOrgDoc-align" title="Search for documents from other providers and agencies" id="findOrgDoc"><div><img src="..//framework/skins/hhsa/images/search_icon.png" /><span>Find Org Documents</span></div></li>
								</c:if>
								<c:if test="${org_type eq 'agency_org'}">
									<li class="findOrgDoc findOrgDoc-align" title="Search through the documents that have been shared with you" id="findOrgDoc"><div style ="width: 170px;"><img src="..//framework/skins/hhsa/images/search_icon.png" /><span>Find Shared Documents</span></div></li>
								</c:if>
								<c:if test="${org_type eq 'city_org' }">
									<li class="selectOrg selectOrg-align" title="View the document vault for specific providers and agencies" id="SelectOrgDoc" onclick="orgOpen()"><div><img src="..//framework/skins/hhsa/images/select_organization.png"/><span>Select Organization</span></div></li>
								</c:if>
								<c:if test="${org_type eq 'agency_org'}">
									<li class="selectOrg selectOrg-align" title="View all the shared documents from a specific organization" id="SelectOrgDoc" onclick="orgOpen()"><div><img src="..//framework/skins/hhsa/images/select_organization.png"/><span>Select Organization</span></div></li>
								</c:if>
							</ul>
						</span>
						<!-- added for defect 8367 -->
						<div id="selectOrgnization" class="leftTreeScroll2" style="display:none">
						<!-- added for defect 8367 -->
						</div>
					</c:if>
				</c:if>
				<c:if test="${org_type ne 'city_org'  && homePageManageOrgFlag ne 'false'}">
					<input type="hidden" id="parentFlag" name="parentFlag" value=""/>
				</c:if>
			</div>
		</div>
			<!-- Restructured grid parameters for R5 -->
			<div  id="documentVaultGrid" class="tabularWrapper tabularWrapperDocumentVault">
			<input type="hidden" name="selectedTree" value="" id="selectedTree"/>
			<input type="hidden" name="clickedSearch" value="${clickedSearch}" id="clickedSearch"/>
			<input type="hidden" name="headerClick" value="${headerClick}" id="headerClick"/>
			<c:if test="${org_type ne 'city_org'  && homePageManageOrgFlag ne 'false'}">
			<input type="hidden" id="sharedSearchJspFlag" value="true"/>
			<input type="hidden" id="sharedSearch" value="${sharedFlag}"/>
			</c:if>
			<c:set var="headDisabled"></c:set>
			<c:if test="${fn:length(documentList) eq 0}">
				<c:set var="headDisabled">disabled</c:set>	
			</c:if>
				<st:table objectName="documentList"  cssClass="heading"
					alternateCss1="evenRows" alternateCss2="oddRows" pageSize='<%=(Integer)session.getAttribute("allowedObjectCount")%>'>
					<c:if test="${check ne 'R' and homePageManageOrgFlag eq false}">
					<st:property toolTip = "Select all" headingName="<input type='checkbox' ${headDisabled} onchange='checkAll(this)' name='chkAll'/>" columnName="documentId" align="center" size="5%" >
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EnhancedDocumentNameExtension" />
					</st:property>
					</c:if>
					<st:property headingName="Name" columnName="docName" align="center" sortType="docName" sortValue="asc" size="30%">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EnhancedDocumentNameExtension" />
					</st:property>
					<st:property headingName="Document Type" columnName="docType" sortType="docType" sortValue="asc" align="center" size="20%"  />
					<st:property headingName="Modified Date" columnName="date"
						align="right" size="25%" sortType="date" sortValue="desc"/>
						
					<c:if test="${homePageManageOrgFlag eq 'false' || org_type eq 'city_org'}">
					<st:property headingName="<img src='../framework/skins/hhsa/images/share_header.png'/>" toolTip="Sort on shared status" columnName="shareStatus" sortType="shareStatus" sortValue="desc"
						align="center" size="5%">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EnhancedDocumentNameExtension" />
					</st:property>
					<st:property headingName="<img src='../framework/skins/hhsa/images/attachment.png'/>" toolTip="Sort on linked status" columnName="linkStatus" sortType="linkStatus" sortValue="desc"
						align="center" size="5%">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EnhancedDocumentNameExtension" />
					</st:property>
					</c:if>
					<st:property headingName="" columnName="fileOptions" align="center" size="5%">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EnhancedDocumentNameExtension" />
					</st:property>
				</st:table>
				<input type="hidden" name="currentFolderId" id="currentFolderId" value="${portletSessionScope.currentFolderId}" />
				<c:choose>
					<c:when test="${fn:length(documentList) eq 0 and clickedSearch eq true}">
						<div class="noRecord" id="noDocumentDiv">No folders or documents found</div>
					</c:when>
					
					<c:when test="${fn:length(documentList) eq 0 and homePageManageOrgFlag eq 'true' and org_type ne 'city_org'}">
						<div class="noRecord" id="noDocumentDiv">No documents have been shared</div>
					</c:when>
					<c:when test="${fn:length(documentList) ne 0 }">
						<div style="display:none" id="noDocumentDiv"></div>
					</c:when>
					<c:otherwise>
						<div class="noRecord" id="noDocumentDiv">No documents currently uploaded</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<!-- Grid Ends -->
	</div>
			
			<div style="display:none" class="noRecord" id="noDocumentDivForNoList">No Records Found</div>
<!-- End -->
</form>
<div class="overlay"></div>
<!--   Jsp Includes -->
<jsp:include page="newfolderOverlay.jsp"></jsp:include>
<jsp:include page="viewfolderprop.jsp"></jsp:include>
<jsp:include page="documentvaultoverlay.jsp"></jsp:include>
<jsp:include page="viewdocumentpropoverlay.jsp"></jsp:include>
<!-- End -->
<c:if test="${param.next_action eq 'openProviderView' or lsAction eq  'openProviderView'}">
<input type="hidden" name="action" value="documentVault" id="searchDocumentVaultId"/>
</c:if>
<input type="hidden" id="isOrganizationSharesDoc" name="isOrganizationSharesDoc" value="${isOrganizationSharesDoc}" />
<input type="hidden" id="checkOrg" name="checkOrg" value="true" />
<div class="alert-box" id="overlayedJSPContent" style="display:none"></div>


<!-- End -->