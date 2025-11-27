<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="java.util.ArrayList, com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/uploadfile.js"></script>
<!--Start Added in R5 -->
<script type="text/javascript" src="../framework/skeletons/hhsa/js/jstree.min.js"></script> 
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<!--End Added in R5 -->
<script type="text/javascript">
var uploadfileForm = null;
var againcall= false;
//on load function to perform various checks on loading of jsp
function onReady(){
	uploadfileForm = document.uploadform.action;
	//else block is added as part of Release 2.6.0 defect:5612	
		if('null' != '<%=request.getAttribute("message")%>' && '' != '<%=request.getAttribute("message")%>'){
			$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" onclick=\"showMe('messagedivover', this)\" />");
			$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagedivover").show();
			
			<%request.removeAttribute("message");%>
			<%session.removeAttribute("message");%>
		}else if(!againcall && "null" != '<%= session.getAttribute("message")%>'){
			$(".messagedivover").html('<%= session.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
			$(".messagedivover").addClass('<%= session.getAttribute("messageType")%>');
			$(".messagedivover").show();
			<% session.removeAttribute("message");
			  session.removeAttribute("messageType");
			%>
			againcall = true;
		}
		
		/* if(document.getElementById("doccategory") != null && "" == document.getElementById("doccategory").value){
			document.getElementById("doctype").disabled = true;
		} */
		//Added for R5- combo box for docType
		if($("#doctype").attr("type") == "text"){
		$("#doctype").typeHeadDropDown({button:$("#combotable_button"), optionBox: $("#dropdownul")});
		}
		//R5 end
		
		// This will execute when any option is selected from Document Category
    	$("#doccategory").change(function() {
    		selectCategory(this.form, '<%=session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE)%>' );
		});	
		
    	// This will execute when Next button is clicked
		$(".alert-box-upload").find('#next1').unbind("click").click(function() { // bind click event to link
			$("#uploadform").validate({
				rules: {
					doccategory: {required: true},
					doctype: {
						required: true,
						typeHeadDropDown: true},
					uploadfile: {required: true},
					docName: {required: true, 
						maxlength: 50, allowSpecialChar: ["A"," _-"]}
				},
				messages: {
					doccategory: {required:"<fmt:message key='REQUIRED_FIELDS'/>"},
					doctype: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
					typeHeadDropDown: "! Please select a valid document type"} ,
					uploadfile:{required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					docName: {required: "<fmt:message key='REQUIRED_FIELDS'/>", 
						maxlength: "<fmt:message key='INPUT_50_CHAR'/>",
						allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_DOCUMENT_NAME'/>"}
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
						$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
						$('#step1').removeClass().addClass('default').css({'padding-left':'15px','margin-left': '25px'});
						$('#step2').removeClass().addClass('active').css('margin-left','-15px');	
					}else{
						$("#tab2").empty();
						$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" onclick=\"showMe('messagedivover', this)\" />");
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

//This will execute when any option is selected from Document Category drop down 
//and will hide - unhide various div depending upon category selected
function selectCategory(form, userOrg){
	var e = document.getElementById('doccategory');
	var category = e.options[e.selectedIndex].value;
	getDocumentTypeList(category, userOrg);
	document.getElementById("doctype").disabled = false;
	
		
}

</script>
<portlet:defineObjects />
<portlet:actionURL var="uploadDocumentUrl" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
	<portlet:param name="workflowId" value="${workflowId}" />
	<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
</portlet:actionURL>
<div class="overlaycontent">
	<form action="${uploadDocumentUrl}" enctype="multipart/form-data" method="post" name="uploadform" id="uploadform">
	<input type="hidden" value="${cancelUploadDocumentUrl}" id="cancelUploadDocument"/>
	<input type="hidden" value="agencyAwardDoc" id="filterDocumentType"/>
	<%-- <c:if test="${document.docCategory ne null}">
		<input type="hidden" name="doccategory" id="doccategory" value="${document.docCategory}"/>
	</c:if> --%>
	<input type="hidden" name="doccategory" id="doccategory" value=""/>
		<div class="formcontainer">
			<div class="messagedivover" id="messagedivover"></div>
			<div class="pad10">Select a document type, then browse your computer for the file to upload.</div>
			<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
			<div class="row" id="typeDiv">
				<span class="label" style='width: 32% !important;'><label class="required">*</label>Document Type:</span>
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
						
				    </c:when>
				    <c:otherwise>
				    	${document.docType}
				    	<input type="hidden" path="doctype" value="${document.docType}" name="doctype" id="doctype" class="input" onkeypress="if (this.value.length > 60) { return false; }" />
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
					<input type="text" id="docName" name="docName" maxlength = "50"/>
				</span>
				<span class="error"></span>
			</div>
			<div class='buttonholder'>
				<input type="button" value="Cancel" name="cancelAwardDoc" id="cancelAwardDoc" class="graybtutton" />
				<c:if test="${disableNext eq 'true'}">
				</c:if>
				<input type="submit" value="Next" name="next1" id="next1" />
			</div>
		</div>
	</form>
</div>
