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
$("#viewdocform").validate({
	rules: {
		docName: {required: true, 
			maxlength: 50, allowSpecialChar: ["A"," _-"]},
		periodcoveredfrom: {required: true},
		periodcoveredto: {required: true},
		implementationstatus: {required: true},
		datelastupdated: {required: true},
		effectivedate: {required: true},
		periodcoveredfromyear: {required: true,
			maxlength: 4, minlength: 4},
		periodcoveredtoyear: {required: true,
			maxlength: 4, minlength: 4},
		meetingdate: {required: true},
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
			date: "<fmt:message key='INVALID_DATE'/>"},
		periodcoveredto: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
		implementationstatus: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
		datelastupdated: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
		effectivedate: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
		periodcoveredfromyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
			minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
			maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
		periodcoveredtoyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
			minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
			maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
		meetingdate: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
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
			document.viewdocform.submit();
		}
	},
	errorPlacement: function(error, element) {
	      error.appendTo(element.parent().parent().find("span.error"));
	}
});
});
</script>
<div class="overlaycontent">
<portlet:defineObjects/>
<portlet:actionURL var="editDocumentPropertiesUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
</portlet:actionURL>
<form id="viewdocform" action="${editDocumentPropertiesUrl}" method ="post" name="viewdocform">
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<input type="hidden" value="${editDocumentPropertiesUrl}" id="editDocumentPropertiesAction"/>
	<input type="hidden" id="docId" value="${document.documentId}">
	<input type="hidden" id="isAddendumType" value="${isAddendumType}" name="isAddendumType">
	<input type="hidden" id="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" name="hiddendocRefSeqNo">
	<input type="hidden" id="docStatus" value="${docStatus}" name="docStatus">
	<input type="hidden" value="${uploadingDocumentType}" id="uploadingDocumentType" name="uploadingDocumentType"/>
	<div class="messagedivover floatNone" id="messagedivover"> </div>
	<% List<DocumentPropertiesBean> docProps = document.getDocumentProperties();%>
	    <%if(request.getAttribute("pageReadOnly")!=null && !(request.getAttribute("pageReadOnly").toString().equalsIgnoreCase("true"))){%>
		    	<h2>Document Information: <span><%=document.getDocName()%></span><label class="linkEdit"><a href="#" title="Edit Properties"  
		    	onclick="javascript:editDocument('<%=document.getDocumentId()%>')" id="edit">Edit Properties</a>
		    	</label></h2>
		    	<div class='hr'></div>
	   		<%}else{%>
		   		<h2>Document Information:<span><%=document.getDocName()%></span></h2>
		 <div class='hr'></div>
   		<%}if(null != request.getAttribute("isLocked") && "true".equalsIgnoreCase((String)request.getAttribute("isLocked"))){%>
   			<script> 
   				$(".messagedivover").html("You can not edit this document as some one else is working on it. Please try after some time."+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
				$(".messagedivover").addClass("failed");
				$(".messagedivover").show();
				$("#edit").attr("disabled", "disabled");
				$("#edit").removeAttr('href');
				$("#edit").removeAttr('onclick');

			</script>
   		<%}%>
   		<%if(("false").equalsIgnoreCase((String) request.getAttribute("EditVersionProp")) && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE))
   				&& (request.getAttribute("isViewDocInfoOrg") == null)){%>
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
   				<script> $("#edit").hide();</script>
   		<%}%>
		<div class="formcontainer">
			<div class="row">
				<span class="label">Document Category:</span>
				<span class="formfield"><%=document.getDocCategory()%></span>
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
			
			<% 	if(null != docProps || docProps.size()>0){
				Integer counter = 1;
				Iterator loIterator = docProps.iterator();
				while(loIterator.hasNext()){
					DocumentPropertiesBean loDocPropsBean = (DocumentPropertiesBean) loIterator.next();
					if(loDocPropsBean.getPropertyType().equalsIgnoreCase("string")){
						if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && loDocPropsBean.isIsdisabled()){
						%>
							<div class="row">
							<span class="label"><label class="required" id="requiredLabel<%=counter%>" style="display:none">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
							<span class="formfield" id="hideWhenClicked<%=counter%>"><%=loDocPropsBean.getPropValue().toString()%>
						<%  
					    }
					    else{
					    	if(loDocPropsBean.getPropDisplayName().equalsIgnoreCase("")){
					       	%>
					       		<%=loDocPropsBean.getPropValue().toString()%></span>
					       		<span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id="editInput<%=counter%>" value="<%=loDocPropsBean.getPropValue().toString()%>" style="display:none"/></span>
					        	 <span class="error"></span>
					        	</div>
					       	<% 	
					       	}
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
					++counter;
					}}}%>			   
			    </div>
			    <div class="buttonholder" id="buttonholder" align="rigth" style="display:none">
					  <input type="button" title="Cancel" id="cancel" value="Cancel" onclick="cancelEdit()" class="graybtutton"/>
					  <input type="submit" title="Save" value="Save"/>
				</div>
</form>
</div>