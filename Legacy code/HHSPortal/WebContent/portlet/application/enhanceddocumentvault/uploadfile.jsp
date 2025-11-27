<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="java.util.ArrayList, com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="../js/enhanceduploadfile.js"></script>
<script type="text/javascript">
var uploadfileForm = document.uploadform.action;
//on load function to perform various checks on loading of jsp
function onReady(){
		if("null" != '<%=request.getAttribute("message")%>'){
			$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivoverError', this)\" />");
			$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagedivover").show();
		}
	
		// This will execute when any option is selected from Document Category
    	$(".alert-box").find("select.terms").change(function() {
    		selectCategory(this.form, '<%=session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE)%>' );
		});	
    	
		$("#doctype").typeHeadDropDown({button:$("#combotable_button"), optionBox: $("#dropdownul")});

    	// This will execute when Next button is clicked
			$("#uploadform").validate({
				rules: {
					doccategory: {required: true},
					doctype: {
						required: true,
						typeHeadDropDown: true
					},
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
					}}},
					/*  Added for Release 5 */
					periodcoveredfrom: {
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50))
					},
					periodcoveredto: {
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						DateRange : new Array("01/01/1800", "12/31/"+(parseInt(new Date().getFullYear())+50)),
						DateToFrom: new Array("periodcoveredfrom",false)
					}/* Release 5 ends */
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
					sampledoctype: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					/*  Added for Release 5 */
					periodcoveredfrom: {
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateRange: "<fmt:message key='REQUIRED_VALID_DATE'/>"
						},
					periodcoveredto: {
							minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
							maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
							DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
							DateRange: "<fmt:message key='REQUIRED_VALID_DATE'/>",
							DateToFrom : "<fmt:message key='REQUIRED_VALID_DATE1'/>"
							}/* Release 5 ends */
				},
				submitHandler: function(form){
						document.uploadform.action=uploadfileForm+'&submit_action=fileinformation&removeNavigator=true&removeMenu=true&isAjaxCall=true&action=enhanceddocumentvault';
						$(document.uploadform).ajaxSubmit(options);
						pageGreyOut();
				},
				errorPlacement: function(error, element) {
				      error.appendTo(element.parent().parent().find(".error"));
				}
			});
			
		    var options = 
		    {
		    	success: function(responseText, statusText, xhr ) 
				{
					var responseString = new String(responseText);
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
		                                callBackInWindow("onReadyStep2");
								}
						// Below classes added when user click Next button following inserting all information in the form.(Step 2)
						$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
						$('#step1').removeClass().addClass('default').css({"margin-left":"25px",'padding-left':'20px'});
						// Changing classes for Step - 2 in Release 5
						$('#step2').addClass('active').css({"margin-left":"-14px"});	
						$('#step3').css({"padding":"0 20px"});
					}else{
						$("#tab2").empty();
						$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivoverError', this)\" />");
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
	removePageGreyOut();
}

// This will execute when any option is selected from Document Category drop down 
// and will hide - unhide various div depending upon category selected
function selectCategory(form, userOrg){
	var e = document.getElementById('doccategory');
	var category = e.options[e.selectedIndex].value;
	if(category == null || category == ""){ 
		document.getElementById("doctype").value=""; 
		document.getElementById("doctype").disabled = true; 
		$('#sampleCategoryDiv').hide();
		$('#sampleTypeDiv').hide();
		return false; 
	}
	if('<%=ApplicationConstants.CITY_ORG%>' == userOrg){
		if("Sample Document" == category){
			getSampleCategory();
			$('#sampleCategoryDiv').show();
			$('#sampleTypeDiv').show();
			$('#typeDiv').hide();
		}
		if("Solicitation" == category){
			getDocumentTypeList(category, userOrg);
			document.getElementById("doctype").disabled = false;
			$('#typeDiv').show();
			$('#sampleCategoryDiv').hide();
			$('#sampleTypeDiv').hide();
		}
		else{
			if("Sample Document" != category){
				$('#sampleCategoryDiv').hide();
				$('#sampleTypeDiv').hide();
			}
			$('#typeDiv').hide();
		}
	 return false;
	}
	else{
		getDocumentTypeList(category, userOrg);
		document.getElementById("doctype").disabled = false;
	} 
}
/* Release 5 ends */
</script>
<portlet:defineObjects />
<div class="overlaycontent">
<!-- Upload Document Overlay -->
	<form action="<portlet:actionURL/>" enctype="multipart/form-data" method="post" name="uploadform" id="uploadform">
		<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
		<div class="formcontainer">
			<div class="messagedivover" id="messagedivoverError"></div>
			<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S032_PAGE, request.getSession())){%>
			<div class="pad10">Select a document type, then browse your computer for the file to upload.</div>
			<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
			<input type="hidden" name="doccategory" id="doccategory" value="<%=document.getDocType()%>"/>
			<%--Combo Box code comment --%>
			<div class="row" id="typeDiv">
				<span class="label"><label class="required">*</label>Document Type:</span>
				<span class="formfield"> 
				<table cellspacing="0" cellpadding="0" border="0" class="ddcombo_table" id="combotable"><tbody>
							<tr>
								<td class="ddcombo_td1">
									<div class="ddcombo_div4" style="background: url(&quot;../framework/skins/hhsa/images/transparent_pixel.gif&quot;) repeat scroll 0% 0% transparent;">
										<input type="text" value="${document.docType}"  name="doctype" id="doctype" class="input" onkeypress="if (this.value.length > 60) { return false; }" />
										<div class="uploadComboBox" style="display:none;" id="optionsBox">
											<ol id= "dropdownul" style="max-height: 180px; overflow: auto;">
												<c:forEach items="${docTypedropDownList}" var="entry">
						       						 <li class="ddcombo_event data">${entry}</li>
						   	 					</c:forEach>
											</ol>
										</div>
										<span class="error" style="width:inherit;"></span>
									</div>
								</td>
								<td valign="top" align="left" class="ddcombo_td2" id="combotable_button"><a></a><img src="../framework/skins/hhsa/images/button2.png" style="display: none;"></td>
							</tr></tbody>
						</table>
						</span>
						</div>
			<%-- <div class="row" id="typeDiv">
				<span class="label"><label class="required">*</label>Document Type:</span>
				<span class="formfield"> 
				    	<input type="text" path="doctype" value="${document.docType}" name="doctype" id="doctype" class="input" onkeypress="if (this.value.length > 60) { return false; }" />
				</span>
				<span class="error docTypeError"></span>
			</div> --%>
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
					<input type="text" id="docName" maxlength = "50" name="docName" />
				</span>
				<span class="error"></span>
			</div>
			<div class='buttonholder'>
				<input type="button" value="Cancel" title="Cancel" name="cancel" id="cancel" class="graybtutton" />
				<input type="submit" value="Next" title="Next" name="next1" id="next1" />
			</div>
			<%} else {%>
				<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
			<%} %>
			<input type="hidden" name="hiddenDocCategory" id="hiddenDocCategory" value='${document.filterDocCategory }'/>	
			<input type="hidden" name="hiddenDocType" id="hiddenDocType" value='<%=document.getFilterDocType()%>'/>
			<input type="hidden" name="hiddenFilterModifiedFrom" id="hiddenFilterModifiedFrom" value='<%=document.getFilterModifiedFrom()%>' />
			<input type="hidden" name="hiddenFilterModifiedTo" id="hiddenFilterModifiedTo" value='<%=document.getFilterModifiedTo()%>' />
			<input type="hidden" name="hiddenFilterProviderId" id="hiddenFilterProviderId" value='<%=document.getFilterProviderId()%>' />
			<input type="hidden" name="hiddenFilterNYCAgency" id="hiddenFilterNYCAgency" value='<%=document.getFilterNYCAgency()%>'/>
			<input type="hidden" name="hiddenDocShareStatus" id="hiddenDocShareStatus" value="<%=document.getDocSharedStatus()%>" />
			<input type="hidden" name="hiddenSampleCategory" value='<%=document.getFilterSampleCategory()%>'/>	
			<input type="hidden" name="hiddenSampleType" value='<%=document.getFilterSampleType()%>'/>
			<input type="hidden" name="sortBy" value='${sortBy}'/>
			<input type="hidden" name="sortType" value='${sortType}'/>
		</div>
	</form>
</div>
