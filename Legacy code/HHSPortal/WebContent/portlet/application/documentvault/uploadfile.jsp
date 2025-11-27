<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="java.util.ArrayList, com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="../js/uploadfile.js"></script>
<script type="text/javascript">
var uploadfileForm = document.uploadform.action;
//on load function to perform various checks on loading of jsp
function onReady(){
		if("null" != '<%=request.getAttribute("message")%>'){
			$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
			$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagedivover").show();
		}
		if('<%=ApplicationConstants.CITY_ORG%>' == '<%=session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE)%>'){
			if('null' != '<%=request.getAttribute("sample")%>' && '<%=request.getAttribute("sample")%>' == 'sample'){
				$('#sampleCategoryDiv').show();
				$('#sampleTypeDiv').show();
			}
			if('null' != '<%=request.getAttribute("docCategory")%>' && '<%=request.getAttribute("docCategory")%>' == 'Solicitation'){
				$('#typeDiv').show();
				
			}else{
				$('#typeDiv').hide();
			}
		}
		if("" == document.getElementById("doccategory").value){
			document.getElementById("doctype").disabled = true;
		}
		// This will execute when any option is selected from Document Category
    	$(".alert-box").find("select.terms").change(function() {
    		selectCategory(this.form, '<%=session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE)%>' );
		});	
    	
    	// This will execute when Next button is clicked
		//$(".alert-box").find('#next1').unbind("click").click(function() { // bind click event to link
			$("#uploadform").validate({
				rules: {
					doccategory: {required: true},
					doctype: {required: {
						depends: function(element) {
		                    return ($("#doccategory").val()!='');
					}}},
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
					doctype: {required: "<fmt:message key='REQUIRED_FIELDS'/>"} ,
					uploadfile:{required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					docName: {required: "<fmt:message key='REQUIRED_FIELDS'/>", 
						maxlength: "<fmt:message key='INPUT_50_CHAR'/>",
						allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_DOCUMENT_NAME'/>"},
					sampledoccategory: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					sampledoctype: {required: "<fmt:message key='REQUIRED_FIELDS'/>"}
				},
				submitHandler: function(form){
					document.uploadform.action=uploadfileForm+'&next_action=fileinformation&removeNavigator=true&removeMenu=true&isAjaxCall=true';
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
						$('#step1').removeClass('active').addClass('default');
						$('#step2').addClass('activeLast');	
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
</script>
<portlet:defineObjects />
<div class="overlaycontent">
	<form action="<portlet:actionURL/>" enctype="multipart/form-data" method="post" name="uploadform" id="uploadform">
		<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
		<div class="formcontainer">
			<div class="messagedivover" id="messagedivover"></div>
			<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S032_PAGE, request.getSession())){%>
			<%-- Start changes for R5 --%>
			<div class="pad10">Select a document type, then browse your computer for the file to upload.</div>
			<%-- End changes for R5 --%>
			<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
			<div class="row">
				<span class="label"><label class="required">*</label>Document Category:</span> 
				<span class="formfield"> 
					<select id="doccategory" name="doccategory" class="terms" style="width: auto"> 
						<c:forEach var="category" items="${document.categoryList}">
							<%String selected = "";%>
								<c:if test="${category==document.docCategory}">
									<%selected = "selected";%>
								</c:if>
							<option value="<c:out value="${category}"/>" <%=selected%> title='${category}'>
							<c:out value="${category}" /></option>
						</c:forEach>
					</select> 
				</span>
				<span class="error"></span>
			</div>
			<div class="row" id="sampleCategoryDiv" style="display: none">
				<span class="label"><label class="required">*</label>Sample Document Category:</span> 
				<span class="formfield">
					<select id="sampledoccategory" name="sampledoccategory" style="width: auto" onchange="selectSampleCategory()">
						<c:forEach var="category" items="${document.sampleCategoryList}">
							<%String selected = "";%>
								<c:if test="${category==document.sampleCategory}">
									<%selected = "selected";%>
								</c:if>
							<option value="<c:out value="${category}"/>" <%=selected%> title='${category}'>
							<c:out value="${category}" /></option>
						</c:forEach>
					</select> 
				</span>
				<span class="error"></span>
			</div>
			<div class="row" id="sampleTypeDiv" style="display: none">
				<span class="label"><label class="required">*</label>Sample Document Type:</span> 
				<span class="formfield">
					<select id="sampledoctype" name="sampledoctype" style="width: auto">
						<c:forEach var="type" items="${document.sampleTypeList}">
							<%String selected = "";%>
								<c:if test="${type==document.sampleType}">
									<%selected = "selected";%>
								</c:if>
							<option value="<c:out value="${type}"/>" <%=selected%> title="${type}">
							<c:out value="${type}" /></option>
						</c:forEach>
					</select> 
				</span>
				<span class="error"></span>
			</div>
			<div class="row" id="typeDiv">
				<span class="label"><label class="required">*</label>Document Type:</span>
				<span class="formfield"> 
					<select id="doctype" name="doctype" class="input" style="width: auto">
					    <option value="" > </option>
						<c:forEach var="type" items="${document.typeList}">
							<%String selected = "";%>
							<c:if test="${type==document.docType}">
								<%selected = "selected";%>
							</c:if>
							<c:if test="${not fn:containsIgnoreCase(type, 'BAFO')}">
							<option value="<c:out value="${type}"/>" <%=selected%> title="${type}">
							</c:if>
							<c:out value="${type}" /></option>
						</c:forEach>
					</select> 
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
					<input type="text" id="docName" name="docName" />
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
