<!--This page is displayed when user navigate to the first overlay while uploading document on document screen.-->
<%@page import="com.sun.org.apache.xalan.internal.xsltc.compiler.sym"%>
<%@page import="javax.portlet.RenderRequest"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects />
<style>
.errorMessages{
	display:none;
}
.formcontainer .row span.label{
	width:36%;
}
.formcontainer .row span.formfield{
	width:29%;
}	
.formcontainer .row span.error{
	float: left;
    padding: 4px 0;
    text-align: left; 
	color:red;
	width:31%;
}
</style>
<script>
//On ready event.
	var documentUploadAction;
	function onReady(){
		$("#docName").keydown(function(evt){
			var keyCode = evt ? (evt.which ? evt.which : evt.keyCode) : event.keyCode;
	        if (keyCode == 13) { 
	        	return false;
	        }
		});
		documentUploadAction= document.uploadBapp.action;
		if("null" != '<%= request.getAttribute("message")%>'){
				$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
				$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
				$(".messagedivover").show();
		}
		$('#docName').alphanumeric( { allow: "-_ "});
		// This will execute when Next button is clicked
		// Changes for R5 starts
		$(".alert-box").find('#next1').unbind("click").click(function() { // bind click event to link
			$("#uploadBapp").validate({
				rules: {
					doccategory: {required: true},
					doctype: {
						required: true,
						typeHeadDropDown: true},
					uploadfile: {required: true},
					docName: {required: true, 
						maxlength: 50,allowSpecialChar: ["A"," _-"]}
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
					//document.uploadform.action=uploadfileForm+'&next_action=fileinformation&removeNavigator=true&removeMenu=true';
					//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
					document.uploadBapp.action = documentUploadAction+'&removeNavigator=true&isAjaxCall=true&removeMenu=true&next_action=fileinformation&doccategory='+'<%=request.getAttribute("document_category")%>'+"&formName="+'<%=request.getAttribute("form_name")%>'+"&formVersion="+'<%=request.getAttribute("form_version")%>'+"&section="+'<%=request.getAttribute("section")%>'+"&subsection="+'<%=request.getAttribute("subsection")%>'+"&serviceAppId="+'<%=request.getAttribute("service_app_id")%>'+"&section_id="+'<%=request.getAttribute("section_id")%>'+'&business_app_id='+'<%=request.getAttribute("business_app_id")%>'+'&service_app_id='+'<%=request.getAttribute("service_app_id")%>'+"&elementId="+'<%=request.getAttribute("elementId")%>';
					document.uploadBapp.action.replaceAll('+','%2B');
					$(document.uploadBapp).ajaxSubmit(options);
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
					 $('#step3').removeClass().addClass('last').css({"margin-left" : "0px"});
					 $('#step1').removeClass().addClass('default').css({"margin-left" : "25px"});
					// Changing classes for Step - 2 in Release 5
					$('#step2').removeClass().addClass('active').css({"margin-left":"-20px","padding-left":""});	
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
	    		// Changes for R5 ends
	});
	$(".alert-box").find('#cancel').unbind("click").click(function() {
			$(".overlay").closeOverlay();
			$('.documentterms').find('option:first').attr('selected', 'selected');
			$('.viewOrRemDoc').find('option:first').attr('selected', 'selected');
	});
	};
	//display document name.
	function displayDocName(filePath){
		var fullPath=filePath.value;
	    var fileNameIndex = fullPath.lastIndexOf("\\") + 1; 
	    var filename = fullPath.substr(fileNameIndex);
	    var docName = document.getElementById("hidden");
	    var ext = filename.lastIndexOf(".");
	    filename = filename.substr(0,ext);
		if(filename.length >= 1){
	    $(".alert-box").find("#docName").val(filename);
	    $(".alert-box").find("#hidden").show();
		}
		else{
			$(".alert-box").find("#hidden").hide();	
		}

	} 
//File page information.
	function fileInfoPage(){
		$(".alert-box").show();
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		document.uploadBapp.action = documentUploadAction+'&removeNavigator=true&removeMenu=true&next_action=fileinformation&docCategory='+'<%=renderRequest.getAttribute("document_category")%>'+"&formName="+'<%=renderRequest.getAttribute("form_name")%>'+"&formVersion="+'<%=renderRequest.getAttribute("form_version")%>'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&serviceAppId="+'<%=renderRequest.getAttribute("service_app_id")%>'+"&section_id="+'<%=renderRequest.getAttribute("section_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+"&elementId="+'<%=renderRequest.getAttribute("elementId")%>';
		document.uploadBapp.submit();
	} /*
	function ValidateForm(form){
		if ( form.uploadfile.value == 0 ) { alert ( "Please select file to upload." ); return false; }
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		form.action = form.action+'&removeNavigator=true&removeMenu=true&next_action=fileinformation&doccategory='+'<%=request.getAttribute("document_category")%>'+"&formName="+'<%=request.getAttribute("form_name")%>'+"&formVersion="+'<%=request.getAttribute("form_version")%>'+"&section="+'<%=request.getAttribute("section")%>'+"&subsection="+'<%=request.getAttribute("subsection")%>'+"&serviceAppId="+'<%=request.getAttribute("service_app_id")%>'+"&section_id="+'<%=request.getAttribute("section_id")%>'+'&business_app_id='+'<%=request.getAttribute("business_app_id")%>'+'&service_app_id='+'<%=request.getAttribute("service_app_id")%>'+"&elementId="+'<%=request.getAttribute("elementId")%>';
		return true;
	} */
</script>
<div class="overlaycontent">
<%-- changes for R5 starts --%>
	<form action="<portlet:actionURL/>" enctype="multipart/form-data" method="post" name="uploadBapp" id="uploadBapp">
		<!-- Modified for Release 3.1.0, Enhancement #6021; Adding new Hidden Type to contain DocType -->
		<input type="hidden" value="${document_type}" id="doctype" name="doctype"/>
		<div class="formcontainer">
			<div class="messagedivover" id="messagedivover"> </div>
			<div class="pad10">Select a document type, then browse your computer for the file to upload.</div>
			<div class="row">
				<span class="label">Document Type:</span>
				<span class="formfield">
				<%=request.getAttribute("document_type")%>
				</span>
			</div>
			<div class="row">
				<span class="label">Select the file to upload:</span>
				<span class="formfield"><input type="file" name="uploadfile" onchange="displayDocName(this)"/></span>
				<span class="error docnameError"></span>
			</div> 
			<div class="row" id="hidden" style="display:none">
				 <span class="label">Document Name:</span>
				 <span class="formfield"><input type="text" id="docName" name="docName" maxlength= "50"/></span>
 				 <span class="error"></span>
			</div>
	 		<div class='buttonholder'>
				<input type="button" value="Cancel" title="Cancel" name="cancel" id="cancel" class="graybtutton"/><input type="submit" value="Next" title="Next" name="next1" id='next1' />
			</div>
		</div>        
	</form>
	<%-- changes for R5 ends --%>
</div>

