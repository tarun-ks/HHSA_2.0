<%-- This jsp is used to display document info to the user from the screen S260--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, java.util.Date, com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/viewDocumentInfo.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript">
var formAction;
//This will execute when Save button is clicked. It will validate form for mandatory fields and the submit the form 
$(document).ready(function() {
	formAction = document.viewdocform.action;
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
$("#viewdocform").validate({
	rules: {
		docName: {required: true, 
			maxlength: 50, allowSpecialChar: ["A"," _-"]},
		periodcoveredfrom: {required: true,
			minlength : 10,
			maxlength : 10,
			DateFormat : true,
			calenderRestrictFutureDate : true,
			DateRange : new Array("01/01/1800", today)},
		periodcoveredto: {required: true,
			minlength : 10,
			maxlength : 10,
			DateFormat : true,
			calenderRestrictFutureDate : true,
			DateRange : new Array("01/01/1800", today),
			DateToFrom: new Array("periodcoveredfrom",false)},
		implementationstatus: {required: true},
		datelastupdated: {required: true,
			minlength : 10,
			maxlength : 10,
			DateFormat : true,
			calenderRestrictFutureDate : true,
			DateRange : new Array("01/01/1800", today)},
		effectivedate: {required: true,
			minlength : 10,
			maxlength : 10,
			DateFormat : true,
			calenderRestrictFutureDate : true,
			DateRange : new Array("01/01/1800", today)},
		periodcoveredfromyear: {required: true,
			maxlength: 4, minlength: 4},
		periodcoveredtoyear: {required: true,
			maxlength: 4, minlength: 4},
		meetingdate: {required: true,
			minlength : 10,
			maxlength : 10,
			DateFormat : true,
			calenderRestrictFutureDate : true,
			DateRange : new Array("01/01/1800", today)},
		helpcategory: {required: true},
		helpradio: {required: true},
		helpdesc: {required: true, 
			maxlength: 250},
		samplecategory: {required: true},
		sampletype: {required: true}
	},
	messages: {
		docName: {required: "<fmt:message key='REQUIRED_FIELDS'/>", 
			maxlength: "<fmt:message key='INPUT_50_CHAR'/>",
			allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_DOCUMENT_NAME'/>"},
		periodcoveredfrom: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
			minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
			calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"},
		periodcoveredto: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
			minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
			DateToFrom : "<fmt:message key='REQUIRED_VALID_DATE1'/>",
			calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"},
		implementationstatus: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
		datelastupdated: {minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			required: "<fmt:message key='REQUIRED_FIELDS'/>",
			DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
			calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"},
		effectivedate: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
		periodcoveredfromyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
			minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
			maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
		periodcoveredtoyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
			minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
			maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
		meetingdate: {minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
			required: "<fmt:message key='REQUIRED_FIELDS'/>",
			DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
			calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"},
		helpcategory: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
		helpradio: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
		helpdesc: {required: "<fmt:message key='REQUIRED_FIELDS'/>", 
			maxlength: "<fmt:message key='INPUT_250_CHAR'/>"},
		samplecategory: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
		sampletype: {required: "<fmt:message key='REQUIRED_FIELDS'/>"}
	},
	submitHandler: function(form){
		var isValid = true;
		$("input[type='text']").each(function(){
	        if($(this).attr("validate")=='calender'){
	              if(!verifyDate(this)){
	            	  isValid = false;
	              }
	        }
	    });
		if(isValid){
			var id = document.getElementById("docId").value;
			pageGreyOut();
			document.viewdocform.action = formAction+'&submit_action=saveDocumentProperties&isAjaxCall=true&documentId='+id;
			var options = 
			{	
			   	success: function(responseText, statusText, xhr ) 
				{
			   		var responseString = new String(responseText);
					var responsesArr = responseString.split("|");
					if(responsesArr[1] == "Error")
					{
						$( "#formcontainer1" ).show();
					}
					else if(responsesArr[1] == "Exception")
					{
						//$(".alert-box-upload").show();
						$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
						$(".messagedivover").addClass(responsesArr[4]);
						$(".messagedivover").show();
						removePageGreyOut();
					}
					else
					{
					    var $response=$(responseText);
	                    var data = $response.contents().find(".overlaycontent");
	                    $("#viewDocumentProperties").html(data.detach());
	                    $("#overlayedJSPContent").html($response);
						$(".overlay").launchOverlay($(".alert-box-viewDocumentProperties"), $(".exit-panel.upload-exit"), "800px", null, "onReady");
						var a=$('.documentLocationPath').text().trim();
						a=a.replace(/\\/g, "&#x200b;\\&#x200b;");
						b='<div style="width:50ch;" ></div>';
						$('.documentLocationPath').html(b);
						$('.documentLocationPath div').html(a);
						$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
						$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
						removePageGreyOut();
					}
					
				},
				error:function (xhr, ajaxOptions, thrownError)
				{                     
					showErrorMessagePopup();
					removePageGreyOut();
				}
			};
$("#viewdocform").ajaxSubmit(options);
			//document.viewdocform.submit();
		}
	},
	errorPlacement: function(error, element) {
	      error.appendTo(element.parent().parent().find("span.error"));
	}
});
});
</script>
<%-- Div for over lay content starts where we keep all the jsp details to display the screen--%>
<div class="overlaycontent">
<portlet:defineObjects/>
<portlet:actionURL var="editDocumentPropertiesUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
		<portlet:param name="isAjaxCall" value="True" />
</portlet:actionURL>
<portlet:resourceURL var="editDocumentProperties" id="editDocumentProperties" escapeXml="false">
		<portlet:param name="action" value="rfpRelease"/>
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="documentId" value="${document.documentId}" />
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if
	test="${(accessScreenEnable eq false)}">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<c:if test="${accessScreenEnable eq false}">
	<%-- Code updated for R4 Starts --%>
	<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record will be displayed in Read-only format.</div>
	<%-- Code updated for R4 Ends --%>
	<script>$("#edit").hide();</script>
</c:if>
<d:content isReadOnly="${(accessScreenEnable eq false) or readOnlyPageAttribute}">
<%-- Form Data Starts This is the main form which will be displayed for S260--%>
<form id="viewdocform" action="${editDocumentPropertiesUrl}" method ="post" name="viewdocform">
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<input type="hidden" value="${editDocumentPropertiesUrl}" id="editDocumentPropertiesAction"/>
	<input type="hidden" id="docId" value="${document.documentId}">
	<input type="hidden" id="isAddendumType" value="${isAddendumType}" name="isAddendumType">
	<input type="hidden" id="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" name="hiddendocRefSeqNo">
	<input type="hidden" id="docStatus" value="${docStatus}" name="docStatus">
	<input type="hidden" value="${uploadingDocumentType}" id="uploadingDocumentType" name="uploadingDocumentType"/>
	<input type="hidden" id="pageReadOnly" value="${pageReadOnly}" name="pageReadOnly">
	<input type="hidden" id="proposalId" value="${proposalId}" name="proposalId">
	<input type="hidden" value="${editDocumentProperties}" id="editDocumentPropertiesResource"/>
	<%-- div to display error message to user if any--%>
	<div class="messagedivover floatNone" id="messagedivover"> </div>
	<% List<DocumentPropertiesBean> docProps = document.getDocumentProperties();%>
	    <%if(request.getAttribute("pageReadOnly")!=null && !(request.getAttribute("pageReadOnly").toString().equalsIgnoreCase("true"))){%>
		    	<h2>Document Information<label class="linkEdit"><a href="#" title="Edit Properties"  
		    	onclick="javascript:editDocument('<%=document.getDocumentId()%>')" id="edit">Edit Properties</a>
		    	</label></h2>
		    	<div class='hr'></div>
	   		<%}else{%>
		   		<h2>Document Information</h2>
		 <div class='hr'></div>
   		<%}if(null != request.getAttribute("isLocked") && "true".equalsIgnoreCase((String)request.getAttribute("isLocked"))){%>
   		<%}%>
   		<%if(("false").equalsIgnoreCase((String) request.getAttribute("EditVersionProp")) && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE))
   				&& (request.getAttribute("isViewDocInfoOrg") == null)){%>
  			<!-- Added as part of Release 3.2.0, to disable Edit Properties link for Linked Documents -->
  			<script> 
   				$(".messagedivover").html("You can only edit the document properties if the application the document is tied to is in a draft, returned, or deferred status");
				$(".messagedivover").addClass("failed");
				$(".messagedivover").show();
				$("#edit").attr("disabled", "disabled");
				$("#edit").removeAttr('href');
				$("#edit").removeAttr('onclick');
			</script>
   		<%} %>
   		<%if(("false").equalsIgnoreCase((String) request.getAttribute("EditVersionProp")) && ApplicationConstants.CITY_ORG.equalsIgnoreCase((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE))){%>
   		<%}%>
   		<%-- Form container Starts This is the main form which will be displayed for S238--%>
		<div class="formcontainer">
		<div class='row'>
						<span class='label'>Document Location: </span> <span
							class="formfield folderPathProp wrap-by-para documentLocationPath"> <%=document.getFilePath()%></span>
					</div>
			<div class="row" id="docType">
				<span class="label">Document Type:</span>
				<span class="formfield"><%=document.getDocType()%></span>
			</div>
			<div class="row">
				<span class="label"><label class="required" id="requiredLabel0" style="display:none">*</label> Document Name:</span>
				<span class="formfield" id="hideWhenClicked0"><%=document.getDocName()%></span>
				<span class="formfield"><input type="text" id="editInput0" name="docName" value="<%=document.getDocName()%>" style="display:none"/></span>
				 <span class="error"></span>
			</div>
			<div class="row">
				<span class="label">File Type:</span>
				<span class="formfield"><%=document.getFileType()%></span>
			</div>
			<div class='row'>
						<span class='label'>Modified By: </span> <span class="formfield"><%=document.getLastModifiedBy()%></span>
					</div>
					<div class='row'>
						<span class='label'>Modified Date: </span> <span class="formfieldTimestamp"><%=document.getDate()%></span>
					</div>
					<div class='row'>
						<span class='label'>Uploaded By: </span> <span class="formfield"><%=document.getCreatedBy()%></span>
					</div>
					<div class='row'>
						<span class='label'>Uploaded Date: </span> <span class="formfieldTimestamp"><%=document.getCreatedDate()%></span>
					</div>
			<% 	if(null != docProps || docProps.size()>0){
				Integer counter = 1;
				Iterator loIterator = docProps.iterator();
				while(loIterator.hasNext()){
					DocumentPropertiesBean loDocPropsBean = (DocumentPropertiesBean) loIterator.next();
					if(loDocPropsBean.getPropertyType().equalsIgnoreCase("string")){
						if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && loDocPropsBean.isIsdisabled()){
						%>
							 			<div class="row">
								       <span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
								       <span class="formfield"><input class= "readonly" type="text" style="width:35px" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" readonly="readonly" value= "<%=loDocPropsBean.getPropValue()%>"/>
								       
						<%  
					    }
					    else{ 
					    	if(loDocPropsBean.getPropDisplayName().equalsIgnoreCase("")){%>
					    			
							         <span class="" id="hideWhenClicked<%=counter%>"><%=loDocPropsBean.getPropValue().toString()%></span>
							         <input type="text" style="width:45px;display:none;" name="<%=loDocPropsBean.getPropertyId()%>" id="editInput<%=counter%>" value="<%=loDocPropsBean.getPropValue().toString()%>" /></span>
					        	 <span class="error"></span>
					        	</div>
					       	<% }
					       	else
					       	{
					       	%>
								<div class="row">
							    	<span class="label"><label class="required" id="requiredLabel<%=counter%>" style="display:none">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
							    	<span class="formfield" id="hideWhenClicked<%=counter%>"><%=loDocPropsBean.getPropValue().toString()%></span>
							    	<span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id="editInput<%=counter%>" value="<%=loDocPropsBean.getPropValue().toString()%>" style="display:none"/></span>
					    			 <span class="error"></span>
					    		</div>
					    	<% 
							}}
							%>
				
					<%}else if(loDocPropsBean.getPropertyType().equalsIgnoreCase("date")){ %>
			    		<div class="row">
						    <span class="label"><label class="required" id="requiredLabel<%=counter%>" style="display:none">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
						    <span class="formfield" id="hideWhenClicked<%=counter%>"><% if(null != loDocPropsBean.getPropValue()){%>
						     	<c:out value="<%=loDocPropsBean.getPropValue()%>"/> 
						     	<% 
						     	}%>
							</span>
							<span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id="editInput<%=counter%>" value="<%=loDocPropsBean.getPropValue().toString()%>" style="display:none"/>
								<img style="display:none" id="openWhenClicked<%=counter%>" src="../framework/skins/hhsa/images/calender.png" class="imgclassUpdated" title="Updated Contract Start Date" onclick="NewCssCal('editInput<%=counter%>',event,'mmddyyyy');return false;"><br/>
								 <span class="error"></span>
							</span>
						</div>
					<%}else if(loDocPropsBean.getPropertyType().equalsIgnoreCase("int")){ %>
			     		<div class="row">
					    	<span class="label"><label class="required" id="requiredLabel<%=counter%>" style="display:none">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
					    	<span class="formfield" id="hideWhenClicked<%=counter%>"><%=Integer.valueOf(loDocPropsBean.getPropValue().toString())%></span>
					    	<span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id="editInput<%=counter%>" value="<%=Integer.valueOf(loDocPropsBean.getPropValue().toString())%>" style="display:none"/></span>
			    			 <span class="error"></span>
			    		</div>
					<%
					}
					++counter;}}%>			   
			    </div>
			    <div class="buttonholder" id="buttonholder" align="rigth" style="display:none">
					  <input type="button" title="Cancel" id="cancel" value="Cancel" onclick="cancelEdit()" class="graybtutton"/>
					  <input type="submit" title="Save" value="Save" />
				</div>
</form>
</d:content>
</div>