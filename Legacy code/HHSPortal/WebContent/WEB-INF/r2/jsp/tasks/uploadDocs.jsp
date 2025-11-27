<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="java.util.ArrayList, com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/uploadfileFinancial.js"></script>
<script type="text/javascript">
//var uploadfileForm = document.uploadform.action;
//on load function to perform various checks on loading of jsp
$(document).ready(function() {
 	onReadyStep1();
});

function onReadyStep1(){
	 if(typeof document.uploadform =='undefined'){
			return false;
		    }
			var uploadfileForm = document.uploadform.action;
				if('null' != '<%=request.getAttribute("message")%>'){
					$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
					$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
					$(".messagedivover").show();
					if('true' != '<%=request.getAttribute("disableNext")%>')
						{
						document.getElementById("next1").disabled = true;
						document.getElementById("doccategory").disabled = true;
						}
					<%request.removeAttribute("message");%>
					<%session.removeAttribute("message");%>
					<%session.removeAttribute("disableNext");%>
				}
				
				$('#doccategory option[value=""]').attr('selected','selected');
				
				disableOnDocCategoryIsEmpty();
				
				// This will execute when any option is selected from Document Category
		    	$("#doccategory").change(function() {
		    		selectCategory(this.form, '<%=session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE)%>' );
		    		disableOnDocCategoryIsEmpty();
				});	
		    	
		  		//Added for R5- combo box functionality
		  		if($("#doctype").attr("type") == "text"){
					$("#doctype").typeHeadDropDown({button:$("#combotable_button"), optionBox: $("#dropdownul")});
		  		}
		  		//End R5
		  		
		    	// This will execute when Next button is clicked
				$(".alert-box-upload").find('#next1').unbind("click").click(function() { // bind click event to link
					$("#uploadform").validate({
						rules: {
							doccategory: {required: true},
							doctype: {
								required: true,
								typeHeadDropDown: true
							/* {
								depends: function(element) {
				                    return ($("#doccategory").val()!='');
							}} */},
							uploadfile: {required: true},
							docName: {required: true, 
								maxlength: 50, allowSpecialChar: ["A"," _-"]},
							sampledoccategory: {required: {
								depends: function(element) {
				                    return ($("#doccategory").val()=='<%=ApplicationConstants.DOC_SAMPLE%>');
							}}},
							sampledoctype: {required: {
								depends: function(element) {
				                    return ($("#sampledoccategory").val()!='');
							}}}
						},
						messages: {
							doccategory: {required:"<fmt:message key='REQUIRED_FIELDS'/>"},
							doctype: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
							
							typeHeadDropDown: "! Please select a valid document type"} ,
							uploadfile:{required: "<fmt:message key='REQUIRED_FIELDS'/>"},
							docName: {required: "<fmt:message key='REQUIRED_FIELDS'/>", 
								maxlength: "<fmt:message key='INPUT_50_CHAR'/>",
								allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_DOCUMENT_NAME'/>"},
							sampledoccategory: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
							sampledoctype: {required: "<fmt:message key='REQUIRED_FIELDS'/>"}
						},
						submitHandler: function(form){
							document.uploadform.action=uploadfileForm+'&submit_action=uploadingFinancialFileInformation&isAjaxCall=true';
							$(document.uploadform).ajaxSubmit(options);
							pageGreyOut();
						},
						errorPlacement: function(error, element) {
						      error.appendTo(element.parent().parent().find("span.error"));
						}
					});
					
				    var options = 
				    {
				    	success: function(responseText, statusText, xhr ) 
						{
				    		if(responseText.indexOf("<head><meta http-equiv")!=-1 && responseText.indexOf("<head><meta http-equiv")<300){
					    	 	var responseText1=responseText.slice(responseText.indexOf('<head><meta http-equiv'),(responseText.indexOf('</head>')+7));
						   		responseText = responseText.replace(responseText1,"");
					    	}
							var responseString = new String(responseText);
							var responsesArr = responseString.split("|");
							if(!(responsesArr[1] == "Error" || responsesArr[1] == "Exception"))
							{
									var $response=$(responseText);
				                    var data = $response.contents().find(".overlaycontent");
				                     $("#tab1").empty();
						 			 $("#tab2").empty();
						 			$("#tabnew").empty();
				                         if(data != null || data != ''){
				                                $("#tab2").html(data.detach());
				                                var overlayLaunchedTemp = overlayLaunched;
												var alertboxLaunchedTemp = alertboxLaunched;
												$("#overlayedJSPContent").html($response);
												overlayLaunched = overlayLaunchedTemp;
												alertboxLaunched = alertboxLaunchedTemp;
				                                callBackInWindow("onReady");
										}
								// Below classes added when user click Next button following inserting all information in the form.(Step 2)
								$('#uniqueTabs ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
								// Release 5 changes starts
								$('#step1').removeClass().addClass('default').css('margin-left','23px');
								$('#step2').addClass('active').css({'margin-left':'-15px','padding-left':'-15px'});	
								// Release 5 changes ends
							}else{
								$("#tab2").empty();
								$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
					            $(".messagedivover").addClass(responsesArr[4]);
					            $(".messagedivover").show();
							}
						removePageGreyOut();
						},
						error:function (xhr, ajaxOptions, thrownError)
						{                     
							showErrorMessagePopup();
							removePageGreyOut();
						}
				    };
			});
			
}
	// This will execute when any option is selected for filter Document Category
$('#sampledoccategory').change(function() {
	if(sampleCategoryForCity()){
	getSampleTypeForCity();
	}	
});

//
function disableOnDocCategoryIsEmpty(){
	if(document.getElementById("doccategory") != null && "" == document.getElementById("doccategory").value){
		document.getElementById("doctype").disabled = true;
	}	
}
// This will execute when any option is selected for filter Document Category
function sampleCategoryForCity() {
	var e = document.getElementById('sampledoccategory');
	var category = e.options[e.selectedIndex].value;
	if (category == null || category == "") {
		document.getElementById("sampledoctype").value = "";
		document.getElementById("sampledoctype").disabled = true;
		return false;
	} else {
		document.getElementById("sampledoctype").disabled = false;
		return true;
	}

}

// This method will get the filter document category and 
//will return the list of document types through servlet call
function getSampleTypeForCity() {
	pageGreyOut();
	var selectedInput = document.getElementById("sampledoccategory").value;
	var url = $("#contextPathSession").val()+"/GetContent.jsp?selectedInput=" + selectedInput+"&organizationId=<%=ApplicationConstants.PROVIDER_ORG%>";
	postRequest(url);
	//$.unblockUI();
	removePageGreyOut();
}

// This will execute when any option is selected from Document Category drop down 
// and will hide - unhide various div depending upon category selected
function selectCategory(form, userOrg){
	var e = document.getElementById('doccategory');
	var category = e.options[e.selectedIndex].value;
	getDocumentTypeList(category, userOrg);
	document.getElementById("doctype").disabled = false;
	
		
}
//Added for Release 5- typeAhead for docType	
var onAutocompleteSelect = function(value, data) {
		document.getElementById("doctype").value=value;
		document.getElementById("doccategory").value=data;
	    isValid = true;
};
//Added for Release 5
var url  =$("#contextPathSession").val()+"/GetContent.jsp?&isFilter=true&requestingtype=financial";
	<c:if test="${org_type eq 'agency_org'}">
	url = $("#contextPathSession").val()+"/GetContent.jsp?&isFilter=true";
	</c:if>
//url = $("#contextPathSession").val()+"/GetContent.jsp?&isFilter=true";
var options = {
 	serviceUrl: url,
   width: 250,
   minChars:3,
   maxHeight:100,
   onSelect: onAutocompleteSelect,
   clearCache: true,
   deferRequestBy: 0, //miliseconds
   params: { city: $("#doctype").val() }
};
//$('#doctype').autocomplete(options);

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
//Added for Release 5 Ends
</script>

<portlet:defineObjects />


 <portlet:actionURL var="uploadDocumentUrl" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" />
	<portlet:param name="proposalId" value="${proposalId}" />
	<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
 </portlet:actionURL>
 <portlet:renderURL var="rfpDocumentRenderUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="render_action" value="displayRFPDocumentList" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="topLevelFromRequest" value="RFPDetails" />
		<portlet:param name="midLevelFromRequest" value="RFPDocuments" />
 </portlet:renderURL>
<input type="hidden" value="${uploadingFileInformation}" id="uploadingFileInformationUrl"/>
<div class="overlaycontent">
	<form action="${uploadDocumentUrl}" enctype="multipart/form-data" method="post" name="uploadform" id="uploadform">
	<input type="hidden" value="${rfpDocumentRenderUrl}" id="rfpDocumentRender"/>
	<c:if test="${document.docCategory ne null}">
	<input type="hidden" name="doccategory" id="doccategory" value="${document.docCategory}" />
	</c:if>
		<c:set var="document" value="${document}"></c:set>
		<div class="formcontainer">
			<div class="messagedivover" id="messagedivover"></div>
			<div class="pad10">Select a document type, then browse your computer for the file to upload.</div>
			<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
			<div class="row" id="typeDiv">
				<span class="label" style='width: 32% !important;'><label class="required">*</label>Document Type:</span>
				<%-- Release 5 changes starts --%>
				<span class="formfield">
				<c:choose>
				    <c:when test="${document.docType eq null}">
				    <table cellspacing="0" cellpadding="0" border="0" class="ddcombo_table" id="combotable"><tbody>
							<tr>
								<td class="ddcombo_td1">
									<div class="ddcombo_div4" style="background: url(&quot;../framework/skins/hhsa/images/transparent_pixel.gif&quot;) repeat scroll 0% 0% transparent;">
										<input type="text" path="doctype" value="${document.docType}" name="doctype" id="doctype" class="input" onkeypress="if (this.value.length > 60) { return false; }" />
										<div style="display:none;margin-left:5px;margin-right:5px;border: 1px solid black;
							background-color: white;
							overflow: hidden;position:absolute;width:276px;
							z-index: 99999;" id="optionsBox">
							<ol id= "dropdownul" style="max-height: 180px; overflow: auto;">
								<c:forEach items="${docTypedropDownCombo}" var="entry">
							        <li class="ddcombo_event data">${entry}</li>
							    </c:forEach>
							</ol>
							<span class="error docTypeError"></span>
						</div>
										<span class="error" style="width:inherit;"></span>
									</div>
								</td>
								<td valign="top" align="left" class="ddcombo_td2" id="combotable_button"><a></a><img src="../framework/skins/hhsa/images/button2.png" style="display: none;"></td>
							</tr></tbody>
						</table>
						<div style="display:none;margin-left:5px;margin-right:5px;border: 1px solid black;
							background-color: white;
							overflow: hidden;position:absolute;width:276px;
							z-index: 99999;" id="optionsBox">
							<ol id= "dropdownul" style="max-height: 180px; overflow: auto;">
								<c:forEach items="${docTypedropDownCombo}" var="entry">
							        <li class="ddcombo_event data">${entry}</li>
							    </c:forEach>
							</ol>
							<span class="error docTypeError"></span>
						</div>
				    </c:when>
				    <c:otherwise>
				    ${document.docType}
				    	<input type="hidden" value="${document.docType}" name="doctype" id="doctype"/>
				    </c:otherwise>
			    </c:choose>
			    </span>
			    <%-- Release 5 changes Ends --%>
				<span class="error"></span>
			</div>
			<div class="row">
				<span class="label" style='width: 32% !important;'><label class="required">*</label>Select the file to upload:</span> 
				<span class="formfield">
					<input type="file" name="uploadfile" onchange="displayDocName(this)"/>
				</span>
				<span class="error docnameError"></span>
			</div>
			<div class="row" id="hidden" style="display: none">
				<span class="label" style='width: 32% !important;'><label class="required">*</label>Document Name:</span> 
				<span class="formfield">
				<%-- Release 5 changes starts --%>
					<input type="text" id="docName" name="docName" maxlength= "50"/>
				<%-- Release 5 changes Ends --%>
				</span>
				<span class="error"></span>
			</div>
			<div class='buttonholder'>
				<input type="button" value="Cancel" title="Cancel" name="cancel" id="cancel" class="graybtutton" />
				<input type="submit" value="Next" title="Next" name="next1" id="next1" />
			</div>
		</div>
	</form>
</div>
