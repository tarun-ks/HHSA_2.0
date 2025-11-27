<!--This page is displayed when user navigate to the second overlay while uploading document on document screen.-->
<%@page import="com.bea.p13n.expression.operator.If"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/calendar.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<%-- Added in R5 start--%>
<link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
<link rel="stylesheet" href="../css/style.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<%-- Added in R5 ends--%>
<portlet:defineObjects/>
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
var documentDisplayFileAction;
var documentUploadAction;
//on load function to perform various checks on loading of jsp
function onReady(){
	documentUploadAction = document.displayfileinfo.action;
	//This will display the error message if a check fails
<%	if(request.getAttribute("message") != null){%>
		var vs = '<%= request.getAttribute("message")%>';
		$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"$('#messagedivover').hide();\" />");
		$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
		$(".messagedivover").show();
		if('<%= request.getAttribute("lbFlag")%>' == "true")
		{
		}
		<%if(request.getAttribute("section") != null && request.getAttribute("section").equals("servicessummary")){%>
		
		if(typeof(vs) != 'undefined' && vs != 'null'){
			$(".messagedivover").html('A document of this type with this name already exists. You cannot replace this document.Click the "Back" button below and rename this document.'+
			"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"$('#messagedivover').hide();\" />");
		}
		else {
		}
<%	}}%>

	$('#periodcoveredfromyear').numeric();
	$("input[name='periodcoveredfromyear']").fieldFormatter("XXXX");
	$('#periodcoveredtoyear').numeric();
	$("input[name='periodcoveredtoyear']").fieldFormatter("XXXX");
	// This will execute when Upload Document button is clicked
	//Submit the form while uploading document
	
	$("input[type='text']").each(function(){
            if($(this).attr("validate")=='calender'){
                  $(this).keypress(function(event) {
                        event = (event) ? event : window.event;
                        var charCode = (event.which) ? event.which : event.keyCode;
                        var isValid = isNumber(this,event);
                   if(isValid){
                         if(charCode==8 || charCode==46){
                              return true; 
                         }else{
                               validateDate(this,event);
                         }
                   }else{
                       return false;
                   }
                  });
                  $(this).keyup(function(event) {
                        event = (event) ? event : window.event;
                        var charCode = (event.which) ? event.which : event.keyCode;
                        if($(this).val().length == 2 || $(this).val().length == 5) {
                              if(charCode==8 || charCode==46){
                                    return true;
                              }else{
                                    $(this).val($(this).val() + '/');
                              }
                        }
                  });
                  $(this).blur(function(event) {
                        verifyDate(this);
                  });
            
            }
      });
      //Updated in R5
	var documentlocationoptions = 
    {
    	success: function(responseText, statusText, xhr ) 
		{	
    	var responseString = new String(responseText);
		// Changing success content for Release 5
		var responsesArr = responseString.split("|");
		if(!(responsesArr[1] == "Error" || responsesArr[1] == "Exception"))
		{
				var $response=$(responseText);
                var data = $response.contents().find("#treeStructure");
                 $("#tab1").empty();
                     if(data != null || data != ''){
                            $("#uploadStep2").hide();
                            $("#uploadStep3").html(data.detach());
                            $("#uploadStep3").show();
                            callBackInWindow("onReadyLocation");
					}
			// Below classes added when user click Next button following inserting all information in the form.(Step 2)
			$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
			$('#step1').removeClass().css({"margin-left":"29px",'padding-left':'10px'});;
            $('#step2').removeClass().addClass('default').css("margin-left","0px");
			$('#step3').removeClass().addClass('active').css("margin-left","-15px");
		}else{
			$("#tabnew").empty();
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
	$(".alert-box").find('#next2').click(function() { // bind click event to link
	$("#displayfileinfo").validate({
	rules: {
					periodcoveredfrom: {
						required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						},
					periodcoveredto: {required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						},
					implementationstatus: {required: true},
					datelastupdated: {required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						},
					effectivedate: {
					required: true,
					minlength : 10,
					maxlength : 10,
					DateFormat : true,
					},
					periodcoveredfromyear: {required: true, 
						maxlength: 4, minlength: 4},
					periodcoveredtoyear: {required: true,
						maxlength: 4, minlength: 4},
					meetingdate: {required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						}
			},
	messages: {
					periodcoveredfrom: {
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						},
					periodcoveredto: {
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						},
					implementationstatus: {
						required: "<fmt:message key='REQUIRED_FIELDS'/>"
						},
					datelastupdated: {
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						},
					effectivedate: {
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						},
					periodcoveredfromyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
						maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"
						},
					periodcoveredtoyear: {
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
						maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"
						},
					meetingdate: {
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
						}
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
							document.displayfileinfo.action = documentUploadAction+'&submit_action=getFolderLocation&isAjaxCall=true';
							$(document.displayfileinfo).ajaxSubmit(documentlocationoptions);
							pageGreyOut();
							}
						},
				errorPlacement: function(error, element) {
				      error.appendTo(element.parent().parent().find("span.error"));
				}
	});

		
	});
	// This will cancel the upload
	$(".alert-box").find('#cancel1').click(function() {
		$(".overlay").closeOverlay();
		$('.documentterms').find('option:first').attr('selected', 'selected');
		$('.viewOrRemDoc').find('option:first').attr('selected', 'selected');
		$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
		$('#step1').removeClass().addClass('active').css({'margin-left': '0px', 'padding-left': '15px'});
		$('#step2').removeClass().css({'margin-left': '0px', 'padding-left': '25px'});
		$('#step3').removeClass().addClass('last');
	});
	
		// This will perform back functionality and give us the first screen
	$(".alert-box").find('#back1').click(function() {
		// bind click event to link
	backRequest(this.form);
	var options = 
  			{	
		   	success: function(responseText, statusText, xhr ) 
			{
				var $response=$(responseText);
                var data = $response.contents().find(".overlaycontent");
                $("#tab1").empty();
	 			$("#tab2").empty();
                if(data != null || data != ''){
					$("#tab1").html(data.detach());
                    var overlayLaunchedTemp = overlayLaunched;
					var alertboxLaunchedTemp = alertboxLaunched;
					$("#overlayedJSPContent").html($response);
					overlayLaunched = overlayLaunchedTemp;
					alertboxLaunched = alertboxLaunchedTemp;
					callBackInWindow("onReady");
				}
				// Back button Reset classes
				$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
				$('#step1').removeClass().addClass('active').css({'margin-left':'0px'});
				$('#step2').removeClass().css({'padding-left':'20px','margin-left':'0px'	});
				$('#step3').removeClass().addClass('last');
				removePageGreyOut();

			},
			error:function (xhr, ajaxOptions, thrownError)
			{                  
				showErrorMessagePopup();
				removePageGreyOut();
			}
		};
		$(this.form).ajaxSubmit(options);
		pageGreyOut();
		return false;
	});	
		
	
}
//This method will submit the form upload the document on the business document screen.
function fileUpload(){
	//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
	document.displayfileinfo.action = documentUploadAction+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&removeNavigator=true&removeMenu=true&next_action=fileupload&docCategory='+'<%=renderRequest.getAttribute("docCategory")%>'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'${elementId}'+"&serviceAppId="+'<%=renderRequest.getAttribute("service_app_id")%>';
	document.displayfileinfo.submit();
} 
//Validation required to be performed 
function ValidateForm1(form){  
	document.displayfileinfo.action = documentUploadAction+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&removeNavigator=true&removeMenu=true&next_action=fileupload'+'&section='+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'${elementId}';
	return true;
}
//Back button functionality  while uploading a document on second screen
function backRequest(form){
	//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
	document.displayfileinfo.action = documentUploadAction+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&removeNavigator=true&removeMenu=true&next_action=backrequest'+'&section='+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&formName="+'<%=renderRequest.getAttribute("form_name")%>'+"&formVersion="+'<%=renderRequest.getAttribute("form_version")%>'+"&docCategory="+'<%=renderRequest.getAttribute("document_category")%>'+"&sectionId="+'<%=renderRequest.getAttribute("section_id")%>';
}
//Cancel button functionality while uploading a document
function cancelAction(){
	window.location.href=$("#contextPath").val()+'/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=businessapplication_1&_urlType=render&wlpbusinessapplication_1_next_action=open&wlpbusinessapplication_1_subsection=documentlist&wlpbusinessapplication_1_section=basic#wlp_businessapplication_1'+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&elementId="+'${elementId}';
}



var formDocumentLocationAction;
var uploadDocumentLocationForm;
function onReadyLocation(){
	formDocumentLocationAction = document.displayfileinfo.action;
	tree($('#hdnopenTreeAjaxUpload').val()+"&action=enhanceddocumentvault", 'overlaytree','customfolderid', "DocumentVault" ,'');
	
	$('#uploadDocument').click(function() {
		$(".alert-box-upload").hide();
		<%	if(request.getAttribute("message") != null){%>
		if('<%= request.getAttribute("message").toString().contains("replace")%>' == "true"){
			document.displayfileinfo.action=$("#uploadDocumenturl").val()+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&removeNavigator=true&removeMenu=true&next_action=fileupload'+'&section='+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'${elementId}'+"&isLinkToApp=true";
		}
		<%}else{%>
		document.displayfileinfo.action=$("#uploadDocumenturl").val()+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&removeNavigator=true&removeMenu=true&next_action=fileupload'+'&section='+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'${elementId}';
		<%}%>
		var uploadDocoptions = {
					success: function(responseText, statusText, xhr ) 
					{
			    		var responseString = new String(responseText);
						var responsesArr = responseString.split("|");
						if(responsesArr[1] == "Error")
						{
							$(".alert-box-upload").show();	
						}else if(responsesArr[1] == "Exception")
						{
							$(".alert-box-upload").show();
							$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"$('#messagedivover').hide();\" />");
							$(".messagedivover").addClass(responsesArr[4]);
							$(".messagedivover").show();
							removePageGreyOut();
						}else{
								
							<%if(request.getAttribute("section").equals("servicessummary")){%>
								window.location.href=$("#contextPath").val()+'/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_add_service&_nfls=false&next_action=open&section='+'<%=request.getAttribute("section")%>'+'&business_app_id='+'<%=request.getAttribute("business_app_id")%>'+'&service_app_id='+'<%=request.getAttribute("service_app_id")%>'+"&subsection="+'<%=request.getAttribute("subsection")%>'+"&elementId="+'${elementId}';
							<%}
							else {%>
								window.location.href=$("#contextPath").val()+'/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_section&_nfls=false&next_action=open&section='+'<%=request.getAttribute("section")%>'+'&business_app_id='+'<%=request.getAttribute("business_app_id")%>'+'&service_app_id='+'<%=request.getAttribute("service_app_id")%>'+"&subsection="+'<%=request.getAttribute("subsection")%>'+"&elementId="+'${elementId}';
							<%}%>														
					}
					
					},
					error:function (xhr, ajaxOptions, thrownError)
					{   
				
					}
				};
		$(document.displayfileinfo).ajaxSubmit(uploadDocoptions);
		pageGreyOut();
		uploadGreyOut();
	});
	
	// This will execute when Next button is clicked
	$(".alert-box").find('#back3').unbind("click").click(function() { // bind click event to link
				//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
				document.displayfileinfo.action = $("#uploadDocumenturl").val()+'&removeNavigator=true&removeMenu=true&next_action=fileinformation&doccategory='+'<%=request.getAttribute("document_category")%>'+"&formName="+'<%=request.getAttribute("form_name")%>'+"&formVersion="+'<%=request.getAttribute("form_version")%>'+"&section="+'<%=request.getAttribute("section")%>'+"&subsection="+'<%=request.getAttribute("subsection")%>'+"&serviceAppId="+'<%=request.getAttribute("service_app_id")%>'+"&section_id="+'<%=request.getAttribute("section_id")%>'+'&business_app_id='+'<%=request.getAttribute("business_app_id")%>'+'&service_app_id='+'<%=request.getAttribute("service_app_id")%>'+"&elementId="+'<%=request.getAttribute("elementId")%>';
				document.displayfileinfo.action.replaceAll('+','%2B');
				$(document.displayfileinfo).ajaxSubmit(back3options);
				pageGreyOut();
		});
    var back3options = 
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
				 $('#step1').removeClass().addClass('default');
				 $('#step2').removeClass().addClass('active').css({'margin-left':'-15px'});
				 $('#step3').removeClass().addClass('last').css({'margin-left':'0px'});
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
 // This will cancel the upload screen-3
	$(".alert-box").find('#cancel3').click(function() {
		$(".overlay").closeOverlay();
		$('.documentterms').find('option:first').attr('selected', 'selected');
		$('.viewOrRemDoc').find('option:first').attr('selected', 'selected');
		$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
		$('#step1').removeClass().addClass('active').css({'margin-left': '0px', 'padding-left': '15px'});
		$('#step2').removeClass().css({'margin-left': '0px', 'padding-left': '25px'});
		$('#step3').removeClass().addClass('last');
	});
}
//updated in R5
</script>
<%-- R5 changes start--%>
<div class="overlaycontent">
	<portlet:defineObjects />
	<form action="<portlet:actionURL/>" method="post" name="displayfileinfo" id="displayfileinfo">
	<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<input type="hidden" value="<%=document.getFilePath()%>" name="filepath">
	<input type="hidden" value="<%=document.getDocCategory()%>" name="documentCategory">
	<input type="hidden" value="<%=document.getDocType()%>" name="documentType">
	<input type="hidden" value="${section}" name="section">
	<input type="hidden" value="${subsection}" name="subsection">
	<!-- Modified for Release 3.1.0, Enhancement #6021; Adding new Hidden Type to contain DocType -->
	<input type="hidden" value="<%=document.getDocType()%>" name="docType">
	<div class="messagedivover" id="messagedivover"> </div>
	<div class="formcontainer" id="uploadStep2">		
		<div class="pad10">Please enter required Document Information, if applicable, and confirm the existing information.<br/>Note: If this is replacing an existing document, any sharing privileges will be applied to this document.</div>	
			<label><font class="required">*</font> Indicates a Required Field</label>
			<%-- <div class="row">
		    	<span class="label">Document Category:</span>
		        <span class="formfield"><%=document.getDocCategory()%></span>
		       
			</div> Commented as a part of Release 5 to hide Category--%>
		  	<div class="row">
		        <span class="label">Document Type:</span>
		        <span class="formfield"><%=document.getDocType()%></span>
		      
		   	</div>
		    <div class="row">
		        <span class="label">Document Name:</span>
		        <span class="formfield"><%=document.getDocName()%></span>
		        
		    </div>
		    <div class="row">
		        <span class="label">File Type:</span>
		        <span class="formfield"><%=document.getFileType()%></span>
		       
		    </div>
		    <% List<DocumentPropertiesBean> docProps = document.getDocumentProperties();
		       Iterator loIterator = docProps.iterator();
		       while(loIterator.hasNext()){
		       		DocumentPropertiesBean loDocPropsBean = (DocumentPropertiesBean) loIterator.next();
		       		
		       		if("string".equalsIgnoreCase(loDocPropsBean.getPropertyType()))
		       		{
	        		 	if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && loDocPropsBean.isIsdisabled())
		       		 	{
					       %>
					       <div class="row">
					       <span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
					       <span class="formfield"><input class= "readonly" style="width:35px" type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" readonly="readonly" value= "<%=loDocPropsBean.getPropValue()%>"/>
						   <%  
			       		 }
			       		 else
			       		 {
				       	 if(loDocPropsBean.getPropDisplayName().equalsIgnoreCase("")){
				       		 	
				       		if(loDocPropsBean.getPropValue()!=null && !loDocPropsBean.getPropValue().toString().equals("")) {%>
				           		<input class= "readonly" type="text" style="width:45px" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" readonly="readonly" value= "<%=loDocPropsBean.getPropValue()%>"/>
				            <%}else{ %>
				             	<input type="text"  style="width:45px" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>"/>
				            <%} %>
						    </span>
						    <span class="error"></span>
						    </div>
						    <% 	
			       		 	}else if(loDocPropsBean.isIsdropdown()){
							%>
							    <div class="row">
					      			<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
					      			<span class="formfield">
								    	<select id = "implementationstatus" name="implementationstatus" class="input">
										<c:forEach var="status" items="${document.implementationStatus}" >
											<option value="<c:out value="${status}"/>"><c:out value="${status}"/></option>
										</c:forEach>
										</select>
									</span>
									<span class="error"></span>
								</div>
							<%     
							}else{
			       		 	%>
								<div class="row">
									<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
								    <span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>"/></span>
						    		<span class="error"></span>
						    	</div>
					    		<% 
							}
			 			}%>
    		 	<% }else if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && "boolean".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%>
    		  		<div class="row">
		       			<span class="label"><font class="required">*</font><%=loDocPropsBean.getPropDisplayName()%>:</span>
		       			<span class="formfield"><input type="checkbox" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>"/></span>
    		 		<span class="error"></span>
    		 		</div>
    		 		<% }else if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && "int".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%>
    		  		<div class="row">
		       			<span class="label"><font class="required">*</font><%=loDocPropsBean.getPropDisplayName()%>:</span>
		       			<span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>"/></span>
    		 		<span class="error"></span>
    		 		</div>
		      		<% }else if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && "date".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%> 		
	   	 			<div class="row">
			    			<span class="label"><font class="required">*</font><%=loDocPropsBean.getPropDisplayName()%>:</span>
		       				<span class="formfield"><input class="<%=loDocPropsBean.getPropertyType()%>" type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" validate="calender" maxlength="10" <%if(loDocPropsBean.getPropValue()!=null && !loDocPropsBean.getPropValue().toString().equals("")) {%> value= "<%=loDocPropsBean.getPropValue()%>"<%} %>/>
				      			<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('<%=loDocPropsBean.getPropertyId()%>',event,'mmddyyyy');return false;"/>
			      			</span>
			      			<span class="error"></span>
    		 		</div>
    			<%
		        }}
			    %>
				<div class='buttonholder'>
					<input type="button" value="Cancel" name="cancel1" id="cancel1"  class="graybtutton"/>
	               	<input type="button" name="back1" name ="back1" id="back1" value="Back" class="graybtutton"/> 
	               	<input type="submit" value="Next" title="Next" name="next2" id="next2" />
				</div> 
		</div>
		<div id="uploadStep3" style="display:none">
		
		</div>		
	</div>
	<input type="hidden" name="isAjaxCall" value="true"/>
	</form>
	<portlet:resourceURL var="openTreeAjax" id="openTreeAjax" escapeXml="false"></portlet:resourceURL>
	<input type="hidden" name="hdnopenTreeAjaxUpload" id ="hdnopenTreeAjaxUpload" value='${openTreeAjax}' />
	<%-- R5 changes ends--%>
</div>