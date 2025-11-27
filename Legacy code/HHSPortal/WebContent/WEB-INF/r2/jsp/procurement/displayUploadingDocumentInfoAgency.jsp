<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/uploadfile.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<script type="text/javascript">
var formAction;
var displayForm;
var validate;
//on load function to perform various checks on loading of jsp
function onReady(){
		formAction = document.accdisplayform.action;
		displayForm = document.accdisplayform;
		
		if("null" != '<%= request.getAttribute("message")%>'){
			$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
			$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagedivover").show();
			if('<%= request.getAttribute("lbFlag")%>' == "true")
			{
				$("#next2city").attr("disabled", "disabled");
			}
		}
		
		if('<%=ApplicationConstants.DOCUMENT_TYPE_HELP%>' == '<%=request.getAttribute("category")%>'){
			$('#helpcategory').show();
			$('#helpradio').show();
			$('#helpdesc').show();
		}
		if('<%=ApplicationConstants.DOC_SAMPLE%>' == '<%=request.getAttribute("category")%>'){
			$('#sampleCategory').show();
			$('#sampleType').show();
		}
		// This will execute when Upload Document button is clicked
		$(".alert-box").find('#next2city').unbind("click").click(function() { 
			$("#accdisplayform").validate({
				rules: {
					helpcat: {required: true},
					help: {required: true},
					docDesc: {required: true}
				},
				messages: {
					helpcat: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					help: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					docDesc: {required: "<fmt:message key='REQUIRED_FIELDS'/>"}
				},
				submitHandler: function(form){
					document.accdisplayform.action=formAction+'&submit_action=uploadFile&isAjaxCall=true&procurementId='+$("#currentProcurementId").val()+"&topLevelFromRequest="+$("#topLevelFromRequest").val()+"&midLevelFromRequest="+$("#midLevelFromRequest").val();
					$(document.accdisplayform).ajaxSubmit(options);
				    $(".alert-box").hide();
					uploadGreyOut();
				},
				errorPlacement: function(error, element) {
				      error.appendTo(element.parent().parent().find("span.error"));
				}
			});
			
			    var options = 
			    {
			    	success: function(responseText, statusText, xhr ) 
					{	
						response = new String(responseText);
						var responses = response.split("|");
						if(responses[1] == "Error")
						{
							$( "#formcontainer1" ).show();
						}
						else if(responses[1] == "Exception")
						{
							$(".alert-box").show();
							$(".messagedivover").html(responses[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
							$(".messagedivover").addClass(responses[4]);
							$(".messagedivover").show();
							removePageGreyOut();
						}
						else
						{
							var url = $("#rfpDocumentRender").val()+"&procurementId="+$("#currentProcurementId").val()+"&topLevelFromRequest="+$("#topLevelFromRequest").val()+"&midLevelFromRequest="+$("#midLevelFromRequest").val(); 
							window.location.href=url;
						}
					},
					error:function (xhr, ajaxOptions, thrownError)
					{   
						showErrorMessagePopup();
						removePageGreyOut();
					}
			    };
			});
}
</script>
<div class="overlaycontent">
	<div id="error"></div>
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<portlet:defineObjects />
	<portlet:renderURL var="rfpDocumentRenderUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="render_action" value="displayRFPDocumentList" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="topLevelFromRequest" value="RFPDetails" />
		<portlet:param name="midLevelFromRequest" value="RFPDocuments" />
	</portlet:renderURL>
	<portlet:actionURL var="uploadDocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
	</portlet:actionURL>
	<form action="${uploadDocumentUrl}" method="post" name="accdisplayform" id="accdisplayform">
	<input type="hidden" value="${rfpDocumentRenderUrl}" id="rfpDocumentRender"/>
	<input type="hidden" value="<%=document.getFilePath()%>" name="filepath">
	<input type="hidden" value="<%=document.getDocCategory()%>" name="documentCategory">
	<input type="hidden" value="<%=document.getDocType()%>" name="documentType">
	<input type="hidden" id="message" name ="message"/>
	<input type="hidden" id="messageType" name ="messageType"/>
	<input type="hidden" id="next_action" name ="next_action"/>
	<div class="messagedivover" id="messagedivover"> </div>
			<div id="formcontainer1" class="formcontainer">
				<div class="pad10">Please enter required Document Information, if applicable, and confirm the existing information.<br/>Note: If this is replacing an existing document, any sharing privileges will be applied to this document.</div>	
				<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
				<div class="row">
					<span class="label">Document Category:</span>
					<span class="formfield"><%=document.getDocCategory()%></span>
				</div>
				
				<div class="row" id="sampleCategory" style="display:none">
					<span class="label">Sample Document Category:</span>
					<span class="formfield"><%=document.getSampleCategory()%></span>
				</div>
				
				<div class="row" id="sampleType" style="display:none">
					<span class="label">Sample Document Type:</span>
					<span class="formfield"><%=document.getSampleType()%></span>
				</div>
				
				<div class="row">
					<span class="label">Document Name:</span>
					<span class="formfield"><%=document.getDocName()%></span>
				</div>
				
				<div class="row">
					<span class="label">File Extension:</span>
					<span class="formfield"><%=document.getFileType()%></span>
				</div>
					    
				<div class="row" id="helpcategory" style="display:none">
					<span class="label"><label class="required">*</label>Help Category:</span>
					<span class="formfield">
						<select id = "helpcat" name="helpcat">
							<c:forEach var="helpcategory" items="${document.helpCategoryList}" >
								<option value="<c:out value="${helpcategory}"/>"><c:out value="${helpcategory}"/></option>
							</c:forEach>
						</select>
					</span>
					<span class="error"></span>
				</div>
				
				<div class='row' id="helpradio" style="display:none">
					<span class='label' style='height: 40px;'> <label class="required">*</label>Display this document on the page specific help page?:</span>
					<span class='formfield'>
						<input type="radio" name="help" value="yes" id="rdoyes" /><label for="rdoyes">Yes</label><br>
						<input type="radio" name="help" value="no" id='rdono' /><label for='rdono' >No</label><br>
					</span>
					<span class="error"></span>
				</div>
				
				<div class="row" id="helpdesc" style="display:none">
	     			<span class="label" style='height: 114px;'><label class="required">*</label>Document Description:</span>
	      			<span class="formfield"><textarea rows="7" cols="30" name="docDesc" id="docDesc" onkeyup="setMaxLength(this,250)" onkeypress="setMaxLength(this,250)"></textarea></span>
	      			<span class="error"></span>
    			</div>
				
				<div class='buttonholder'>
					<input type="button" value="Cancel" title="Cancel" name="cancel1" id="cancel1"  class="graybtutton"/>
		            <input type="button" name="back1" title="Back" name ="back1" id="back1" value="Back" class="graybtutton"/> 
		            <input type="submit" value="Upload Document" title="Upload Document" name="next2city" id="next2city" />
				</div> 
			</div>
	</form>
</div>

