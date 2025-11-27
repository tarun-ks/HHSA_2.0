<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="java.util.ArrayList, com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/uploadfile.js"></script>
<script type="text/javascript">
var uploadfileForm = document.uploadform.action;
//on load function to perform various checks on loading of jsp
function onReady(){
		if('null' != '<%=request.getAttribute("message")%>'){
			$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
			$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagedivover").show();
			if('null' != '<%=request.getAttribute("disableNext")%>' && 'true' == '<%=request.getAttribute("disableNext")%>')
				{
				document.getElementById("next1").disabled = true;
				document.getElementById("doccategory").disabled = true;
				}
			<%request.removeAttribute("message");%>
			<%session.removeAttribute("message");%>
			<%session.removeAttribute("disableNext");%>
		}
		if(document.getElementById("doccategory") != null && "" == document.getElementById("doccategory").value){
			document.getElementById("doctype").disabled = true;
		}
		// This will execute when any option is selected from Document Category
    	$("#doccategory").change(function() {
    		selectCategory(this.form, '<%=session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE)%>' );
		});	
    	
   		//Added for R5- doctype combo box
		<c:if test="${document.docType eq null}">
			$("#doctype").typeHeadDropDown({button:$("#combotable_button"), optionBox: $("#dropdownul")});
		</c:if>
		//R5 end
    	// This will execute when Next button is clicked
		//$(".alert-box-upload").find('#next1').unbind("click").click(function() { 
			$("#uploadform").validate({
				rules: {
					doccategory: {required: true},
					doctype: {required: true,
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
					document.uploadform.action=uploadfileForm+'&submit_action=uploadingFileInformation&isAjaxCall=true';
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
					var responseString = new String(responseText);
					//var revisedResponse = responseString.substring(responseString.indexOf("<body>")+6, responseString.indexOf("</body>"));
					var responsesArr = responseString.split("|");
					if(!(responsesArr[1] == "Error" || responsesArr[1] == "Exception"))
					{  
							var $response=$(responseText);
		                    var data = $response.contents().find(".overlaycontent");
		                     $("#tab1").empty();
				 			 $("#tab2").empty();
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
						// Start Changes in R5
						$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
						$('#step1').removeClass().addClass('default').css('margin-left','25px');
						$('#step2').removeClass().addClass('active').css('margin-left', '-14px');
						$('#step3').removeClass().addClass('last').css('padding-left', '20px');
						// End Changes in R5
					}else{
						$("#tab2").empty();
						$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
			            $(".messagedivover").addClass(responsesArr[4]);
			            $(".messagedivover").show();
					}
					$('#btnholder').hide();
				removePageGreyOut();
				},
				error:function (xhr, ajaxOptions, thrownError)
				{                     
					showErrorMessagePopup();
					removePageGreyOut();
				}
		    };
	//});
	 
	
}
	// This will execute when any option is selected for filter Document Category
$('#sampledoccategory').change(function() {
	if(sampleCategoryForCity()){
	getSampleTypeForCity();
	}	
});
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
</script>
<portlet:defineObjects />
<portlet:actionURL var="uploadDocumentUrl" escapeXml="false">
	<portlet:param name="action" value="rfpRelease" />
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" />
	<portlet:param name="proposalId" value="${proposalId}" />
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="organizationId" value="${organizationId}" />
	<portlet:param name="staffId" value="${staffId}" />
	<portlet:param name="userName" value="${userName}" />
	<%-- Code updated for R4 Ends --%>
	<portlet:param name="awardId" value="${awardId}" />
	<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
	<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	<portlet:param name="replacingDocumentId" value="${replacingDocumentId}" />
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="uploadProcess" value="${uploadProcess}" />
	<portlet:param name="hiddenAddendumType" value="${isAddendumType}" />
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
	<portlet:param name="isFinancials" value="${isFinancials}" />
	<portlet:param name="hdncontractId" value="${hdncontractId}" />
	<portlet:param name="asProcStatus" value="${asProcStatus}" />
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>
<%-- Code updated for R4 Ends --%>
</portlet:actionURL>

<portlet:actionURL var="cancelUploadDocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="submit_action" value="cancelUploadActionStep1" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
		<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
		<portlet:param name="organizationId" value="${organizationId}" />
		<portlet:param name="isFinancials" value="${isFinancials}" />
		<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>
</portlet:actionURL>

<div class="overlaycontent">
	<form action="${uploadDocumentUrl}" enctype="multipart/form-data" method="post" name="uploadform" id="uploadform">
	<input type="hidden" value="${rfpDocumentRenderUrl}" id="rfpDocumentRender"/>
	<input type="hidden" value="${cancelUploadDocumentUrl}" id="cancelUploadDocument"/>
	<input type="hidden" value="${uploadingDocumentType}" id="uploadingDocumentType"/>
	<input type="hidden" id="awardId" value="${awardId}" name="awardId"/>
	<input type="hidden" id="replacingDocumentId" value="${replacingDocumentId}" name="replacingDocumentId"/>
	<%-- Start : Changes in R5 --%>
	<input type="hidden" value="RFP" id="filterDocumentType"/>
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
	<input type = 'hidden' value='${hdncontractId}' id='hdncontractId' name='hdncontractId' />
	<input type="hidden" id="asProcStatus" value="${asProcStatus}" name="asProcStatus"/>
	<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>
		<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
		<input type="hidden" name="doccategory" id="doccategory" value="<%=document.getDocCategory()%>"/>
		<div class="formcontainer">
			<div class="messagedivover" id="messagedivover"></div>
			<div class="pad10">Select a document type, then browse your computer for the file to upload.</div>
			<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
			<div class="row" id="typeDiv">
				<span class="label"><label class="required">*</label>Document Type:</span>
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
										<span class="error" style="width:inherit;" ></span>
									</div>
								</td>
								<td valign="top" align="left" class="ddcombo_td2" id="combotable_button"><a></a><img src="../framework/skins/hhsa/images/button2.png" style="display: none;"></td>
							</tr></tbody>
						</table>
						
				    </c:when>
				    <c:otherwise>
				    	${document.docType}
				    	<input type="hidden" value="${document.docType}" name="doctype" id="doctype" class="input"/>
				    </c:otherwise>
			    </c:choose>
				</span>
				<span class="error"></span>
			</div>
			<div class="row">
				<span class="label"><label class="required">*</label>Select the file to upload:</span> 
				<span class="formfield">
					<input type="file" name="uploadfile" onchange="displayDocName(this)"/>
				</span>
				<span class="error docnameError"></span>
			</div>
			<div class="row" id="hidden" style="display: none">
				<span class="label"><label class="required">*</label>Document Name:</span> 
				<span class="formfield">
					<input type="text" id="docName" name="docName" maxlength= "50"/>
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
