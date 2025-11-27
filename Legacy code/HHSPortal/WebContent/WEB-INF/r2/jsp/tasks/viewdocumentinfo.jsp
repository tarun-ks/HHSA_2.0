<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, java.util.Date, com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant,com.nyc.hhs.util.DateUtil,com.nyc.hhs.constants.HHSR5Constants, org.apache.commons.lang.StringEscapeUtils" %>
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
	onReadyForViewInfo();
	if("taskReturnedPaymentReview" == $("#hdnTaskType").val()){
		$("#edit").hide();
	}
});
function onReadyForViewInfo(){
	formAction = document.viewdocform.action;
	if('null' != '<%=session.getAttribute("message")%>' && '<%=session.getAttribute("messageType")%>' != 'confirmation'){
		$(".messagedivover").html('${message}'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
		$(".messagedivover").addClass('<%= session.getAttribute("messageType")%>');
		$(".messagedivover").show();
		<%session.removeAttribute("message");%>
		<%session.removeAttribute("messageType");%>
	}
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
			document.viewdocform.action = formAction+'&submit_action=saveFinancialDocumentProperties&isAjaxCall=true&documentId='+id;
			$("#viewdocform").ajaxSubmit(options);
			//document.viewdocform.submit();
		}
	},
	errorPlacement: function(error, element) {
	      error.appendTo(element.parent().parent().find("span.error"));
	}
});

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
			$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
			$(".messagedivover").addClass(responsesArr[4]);
			$(".messagedivover").show();
			removePageGreyOut();
		}// Code updated for R4 starts --%>
		else if(responsesArr[4] == "failed"){
			removePageGreyOut();
			$("#errormessagediv").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			$("#errormessagediv").addClass(responsesArr[4]);
			$("#errormessagediv").show();
		}
		// Code updated for R4 Ends --%>
		else
		{
			<%--R5 Update Start: for Edit properties--%>
			if(responseText.indexOf("<head><meta http-equiv")!=-1 && responseText.indexOf("<head><meta http-equiv")<300){
	    	 	var responseText1=responseText.slice(responseText.indexOf('<head><meta http-equiv'),(responseText.indexOf('</head>')+7));
		   		responseText = responseText.replace(responseText1,"");
	    	}
			var $response=$(responseText);
            var data = $response.contents().find(".overlaycontent");
            $("#viewDocumentProperties").html(data.detach());
            $("#overlayedJSPContent").html($response);
			$(".overlay").launchOverlay($(".alert-box-viewDocumentProperties"), $(".exit-panel.upload-exit"), "650px", null, "onReadyForViewInfo");
			var a=$('.documentLocationPath').text().trim();
			a=a.replace(/\\/g, "&#x200b;\\&#x200b;");
			b='<div style="width:50ch;" ></div>';
			$('.documentLocationPath').html(b);
			$('.documentLocationPath div').html(a);
			removePageGreyOut();
			<%--R5 Update End: for Edit properties--%>
		}
	},
	error:function (xhr, ajaxOptions, thrownError)
	{   
		showErrorMessagePopup();
		removePageGreyOut();
	}
};
}
function showText(){
	$("#DocumentNameId").show();
}
function hideText(){
	$("#DocumentNameId").hide();
}

</script>

<div class="overlaycontent">
<portlet:defineObjects/>
<portlet:actionURL var="editDocumentPropertiesUrl" escapeXml="false">
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
	<!-- QC 9614 R 9.3 put docTypeHidden value in  double quotes to preserve apostrophe -->	
	<input type="hidden" name="docTypeHidden" id="docTypeHidden" value="${document.docType}" /> 
	
	<input type="hidden" value="" id="hdnOrgType" name="hdnOrgType"/>
	<input type="hidden" value="" id="hdnEditable" name="hdnEditable"/>
	<div class="messagediv" id="errormessagediv"></div>
	<div class="messagedivover floatNone" id="messagedivover"> </div>
	<% List<DocumentPropertiesBean> docProps = document.getDocumentProperties();%>
	    <%if(request.getAttribute("docStatus")!=null && document.getDocumentId()!=null && !(request.getAttribute("docStatus").toString().equalsIgnoreCase("Submitted"))){%>
		    	<h2>Document Information: <span><%=document.getDocName()%></span>
		    	<c:if test="${hdnOrgType eq org_type && hdnEditable eq 'true' }">
		    	<label class="linkEdit"><a href="#" title="Edit Properties"  
		    	onclick="javascript:editDocument('<%=document.getDocumentId()%>');showText();" id="edit">Edit Properties</a>
		    	</label></c:if></h2>
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
				$("#edit").attr("disabled", "disabled");
				$("#edit").removeAttr('href');
				$("#edit").removeAttr('onclick');

			</script>
   		<%} %>
   		<%if(("false").equalsIgnoreCase((String) request.getAttribute("EditVersionProp")) && ApplicationConstants.CITY_ORG.equalsIgnoreCase((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE))){%>
   				<script> $("#edit").hide();</script>
   		<%}%>
		<div class="formcontainer">
		<!-- release 5 change starts -->
		<div class='row'>
						<span class='label'>Document Location: </span> <span
							class="formfield folderPathProp wrap-by-para documentLocationPath"> <%=document.getFilePath()%></span>
					</div>
			<!-- release 5 change Ends -->
			<div class="row" id="docType">
				<span class="label">Document Type:</span>
				<span class="formfield"><%=document.getDocType()%></span>
			</div>
			<div class="row">
				<span class="label">Document Name:</span>
				<span class="formfield" id="hideWhenClicked0"><%=document.getDocName()%></span>
				<span class="formfield" id="DocumentNameId" style="display:none"><label class="required" id="requiredLabel0" style="display:none">*</label><input type="text" id="editInput0" name="docName" value="<%=document.getDocName()%>" style="display:none"/></span>
				 <span class="error"></span>
			</div>
			<div class="row">
				<span class="label">File Type:</span>
				<span class="formfield"><%=document.getFileType()%></span>
			</div>
			<!-- release 5 change starts -->
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
				<!-- release 5 change Ends -->
			<% 	if(null != docProps && docProps.size()>0){
				Integer counter = 1;
				Iterator loIterator = docProps.iterator();
				while(loIterator.hasNext()){
					DocumentPropertiesBean loDocPropsBean = (DocumentPropertiesBean) loIterator.next();
					if(loDocPropsBean.getPropertyType().equalsIgnoreCase("string")){
						if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && loDocPropsBean.isIsdisabled()){
						%>
							<div class="row">
							<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
							<span class="formfield" id="hideWhenClicked<%=counter%>"><%=loDocPropsBean.getPropValue().toString()%>
						<%  
					    }
					    else{
					    	if(loDocPropsBean.getPropDisplayName().equalsIgnoreCase("")){
					       	%>
					       		<%=loDocPropsBean.getPropValue().toString()%></span>
					       		<span class="formfield"><label class="required" id="requiredLabel<%=counter%>" style="display:none">*</label><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id="editInput<%=counter%>" value="<%=loDocPropsBean.getPropValue().toString()%>" style="display:none"/></span>
					        	 <span class="error"></span>
					        	</div>
					       	<% 	
					       	}
					       	else
					       	{
					       	%>
								<div class="row">
							    	<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
							    	<span class="formfield" id="hideWhenClicked<%=counter%>"><%=loDocPropsBean.getPropValue().toString()%></span>
							    	<span class="formfield"><label class="required" id="requiredLabel<%=counter%>" style="display:none">*</label><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id="editInput<%=counter%>" value="<%=loDocPropsBean.getPropValue().toString()%>" style="display:none"/></span>
					    			 <span class="error"></span>
					    		</div>
					    	<% 
							}}
							%>
				
					<%}else if(loDocPropsBean.getPropertyType().equalsIgnoreCase("date")){ %>
			    		<div class="row">
						    <span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
						    <%-- Start : Update Changes in R5 --%>
						    <%String lsDate = DateUtil.getDateByFormat(HHSR5Constants.HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY,HHSR5Constants.MMDDYYFORMAT,loDocPropsBean.getPropValue().toString());%>
						    <span class="formfield" id="hideWhenClicked<%=counter%>"><% if(null != loDocPropsBean.getPropValue()){%>
						     	<c:out value="<%=lsDate%>"/> 
						     	<% 
						     	}%>
							</span>
							 <%-- End : Update Changes in R5 --%>
							<!-- release 5 change starts -->
							<span class="formfield"><label class="required" id="requiredLabel<%=counter%>" style="display:none">*</label><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id="editInput<%=counter%>" value="<%=lsDate%>" style="display:none"/>
							<!-- release 5 change starts -->
								<img style="display:none" id="openWhenClicked<%=counter%>" src="../framework/skins/hhsa/images/calender.png" class="imgclassUpdated" title="Updated Contract Start Date" onclick="NewCssCal('editInput<%=counter%>',event,'mmddyyyy');return false;"><br/>
								 <span class="error"></span>
							</span>
						</div>
					<%}else if(loDocPropsBean.getPropertyType().equalsIgnoreCase("int")){ %>
			     		<div class="row">
					    	<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
					    	<span class="formfield" id="hideWhenClicked<%=counter%>"><%=Integer.valueOf(loDocPropsBean.getPropValue().toString())%></span>
					    	<span class="formfield"><label class="required" id="requiredLabel<%=counter%>" style="display:none">*</label><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id="editInput<%=counter%>" value="<%=Integer.valueOf(loDocPropsBean.getPropValue().toString())%>" style="display:none"/></span>
			    			 <span class="error"></span>
			    		</div>
					<%
					}
					++counter;
					}}%>			   
			    </div>
			    <div class="buttonholder" id="buttonholder" align="right" style="display:none">
					  <input type="button" title="Cancel" id="cancel" value="Cancel" onclick="cancelEdit();hideText();" class="graybtutton"/>
					  <input type="submit" title="Save" value="Save"/>
				</div>
</form>
</div>