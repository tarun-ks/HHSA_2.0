<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/uploadfileFinancial.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<!-- Release 5 changes Starts -->
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/documentlist.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/style.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/jstree.min.js"></script> 
<!-- Release 5 changes Ends -->
<script type="text/javascript">
var formAction;
var displayuploadForm;
var validate;
var againcall= false;
//on load function to perform various checks on loading of jsp
function onReady(){
	onReadyStep2();
}
function onReadyStep2(){
	  if(typeof document.displayuploadForm =='undefined'){
		return false;
	    }
		validate = true;
		formAction = document.displayuploadForm.action;
		displayuploadForm = document.displayuploadForm;
	  //else block is added as part of Release 2.6.0 defect:5612	
		if("null" != '<%= request.getAttribute("message")%>'){
			$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivovertask', this)\" />");
			$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagedivover").show();
			
		}else if(!againcall && "null" != '<%= session.getAttribute("message")%>'){
			$(".messagedivover").html('<%= session.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivovertask', this)\" />");
			$(".messagedivover").addClass('<%= session.getAttribute("messageType")%>');
			$(".messagedivover").show();
			<% session.removeAttribute("message");
			  session.removeAttribute("messageType");
			%>
			againcall = true;
		}
		
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

		 $('#periodcoveredfromyear').numeric(); 
		$("input[name='periodcoveredfromyear']").fieldFormatter("XXXX");
		 $('#periodcoveredtoyear').numeric(); 
		$("input[name='periodcoveredtoyear']").fieldFormatter("XXXX");
		// This will execute when Upload Document button is clicked
		$(".alert-box").find('#next2').unbind("click").click(function() {
			$("#displayuploadForm").validate({
				rules: {
					periodcoveredfrom: {
						required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						},
					periodcoveredto: {
						required: true,
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
					effectivedate: {required: true,
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
					periodcoveredfrom: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						},
					periodcoveredto: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						},
					implementationstatus: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					datelastupdated: {minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						},
					effectivedate: {minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						},
					periodcoveredfromyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
						maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
					periodcoveredtoyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
						maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
					meetingdate: {minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
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
						document.displayuploadForm.action= $("#filelocation").val()+"&isAjaxCall=true";
						$(document.displayuploadForm).ajaxSubmit(options);
						pageGreyOut();
					}
				},
				errorPlacement: function(error, element) {
				      error.appendTo(element.parent().parent().find("span.error"));
				}
			});
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
	                    var data = $response.contents().find("#treeStructure");
	                    $("#tab1").empty();
			 			$("#tab2").empty();
			 			$("#tabnew").empty();
	                         if(data != null || data != ''){
	                                $("#tabnew").html(data.detach());
	                                var overlayLaunchedTemp = overlayLaunched;
									var alertboxLaunchedTemp = alertboxLaunched;
									//$("#overlayedJSPContent").html($response);
									overlayLaunched = overlayLaunchedTemp;
									alertboxLaunched = alertboxLaunchedTemp;	
	                                //callBackInWindow("onReadyStep3");
									onReadyStep3();
							}
					// Below classes added when user click Next button following inserting all information in the form.(Step 2)
					$('#step1').removeClass().css({"margin-left":"14px","padding-left":"9px"});	
					$('#step2').removeClass().addClass('default').css({"margin-left":"0px"});
					$('#step3').removeClass().addClass('active').css({"margin-left":"-20px","padding-left":""});	
				}else{
					//$("#tabnew").empty();
					$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivovertask', this)\" />");
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
		
		
		//Added Method in R5
		$(".alert-box-upload").find('#back2').unbind("click").click(
				function() {
					$("#displayuploadForm").submit(); 
					 $("#uploadStep3").hide();
		            $('#step1').removeClass().addClass('default');
					$('#step2').removeClass().addClass('active').css({'margin-left':'-14px'});
					$('#step3').removeClass().addClass('last').css({'margin-left':'0px'});
		            $("#formcontainer1").show();
					});
		
}
//end
//Added code for release 5 for folder structure
/* function onReadyLocation(){
	$js("#overlaytree").jstree("destroy");
	tree($('#hdnopenTreeAjaxUpload').val(), 'overlaytree','customfolderid', '' ,'');
	 
} */
function onReadyStep3(){
	if($js("#overlaytree").size()>0){
		$js("#overlaytree").jstree("destroy");
	    tree($('#hdnopenTreeAjaxUpload').val()+"&action=enhanceddocumentvault", 'overlaytree','customfolderid', "DocumentVault" ,'');
	}
	if("null" != '<%= request.getAttribute("message")%>'){
		$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
		$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
		$(".messagedivover").show();
	}
	var uploadoptionsfinancial = 
		    {
		    	success: function(responseText, statusText, xhr ) 
				{	
		    		// added below line for taskheader misplaces in task screen
		    		$('.portlet1Col').css("position", "");
		    		// Changes end
		    		if(responseText.indexOf("<head><meta http-equiv")!=-1 && responseText.indexOf("<head><meta http-equiv")<300){
			    	 	var responseText1=responseText.slice(responseText.indexOf('<head><meta http-equiv'),(responseText.indexOf('</head>')+7));
				   		responseText = responseText.replace(responseText1,"");
			    	}
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
						//removePageGreyOut();
					}
					else
					{
						var $response=$(responseText);
	                    var data = $response.contents().find("#documentSection");
	                    if(data!=null && data!=""){
	                    $("#documentWrapper").html(data.detach());
	                	$(".messagediv").html('Document uploaded successfully'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
						$(".messagediv").addClass('passed');
						$(".messagediv").show();
	                    }
	                   $(".overlay").closeOverlay();
					}
					removePageGreyOut();
				},
				error:function (xhr, ajaxOptions, thrownError)
				{   
					showErrorMessagePopup();
					removePageGreyOut();
				}
		    };
		$('#uploadDocumentFinancial').click(function() {
			pageGreyOut();
			document.uploadDocumentLoc.action=$("#uploadDocumenturl").val()+"&isAjaxCall=true";
			$("#uploadDocumentLoc").ajaxSubmit(uploadoptionsfinancial);
		    $(".alert-box").hide();
		   
			uploadGreyOut();
			
		});
	
	$(".alert-box")
	.find('#back2')
	.unbind("click")
	.click(
			function() {
				pageGreyOut();
				uploadDocumentLoc.action = $("#uploadbackStep3").val();
				var options = {
					success : function(responseText, statusText, xhr) {
						var $response = $(responseText);
						var data = $response.contents().find(
								".overlaycontent");
						$("#tab1").empty();
						$("#tab2").empty();
						$("#tabnew").empty();
						if (data != null || data != '') {
							$("#tab2").html(data.detach());
							var overlayLaunchedTemp = overlayLaunched;
							var alertboxLaunchedTemp = alertboxLaunched;
							$("#overlayedJSPContent").html($response);
							overlayLaunched = overlayLaunchedTemp;
							alertboxLaunched = alertboxLaunchedTemp;
							callBackInWindow("onReady");
							$('#step1').removeClass().addClass(
									'default').css({
										"margin-left" : "25px"
										
									});
							$('#step2').removeClass().addClass(
									'active').css({
								"margin-left" : "-20px",
								"padding-left" : ""
							});
							 $('#step3').removeClass().addClass(
									'last').css({
								    "margin-left" : "0px",
								    "padding-left" : "20px"
							});
							    }
						removePageGreyOut();

					},
					error : function(xhr, ajaxOptions, thrownError) {
						showErrorMessagePopup();
						removePageGreyOut();
					}
				};
				pageGreyOut();
				$(uploadDocumentLoc).ajaxSubmit(options);
				return false;
			});
	$(".alert-box-upload").find('#cancel2').unbind("click").click(function() {
		cancelUploadDocuments();	
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
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="topLevelFromRequest" value="RFPDetails" />
		<portlet:param name="midLevelFromRequest" value="RFPDocuments" />
	</portlet:renderURL>
	<portlet:actionURL var="uploadDocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" />
		<portlet:param name="currentProcurementId" value="${procurementId}" />
		<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
	</portlet:actionURL>
	<portlet:resourceURL var="cancelUploadDocument" id="cancelUploadDocument" escapeXml="false">
	</portlet:resourceURL>
	<%-- Release 5 changes start --%>
		<portlet:actionURL var="uploadDocumentBack1" escapeXml="false">
		</portlet:actionURL>
		<portlet:actionURL var="filelocationUrl" escapeXml="false">
		<portlet:param name="submit_action" value="fileLocation" />	
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<portlet:param name="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" />
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
		<portlet:param name="proposalId" value='${proposalId}'/>
		<portlet:param name="topLevelFromRequest" value="RFPDetails" />
		<portlet:param name="midLevelFromRequest" value="RFPDocuments" />
	</portlet:actionURL>
	
	<form action="#" method="post" name="displayuploadForm" id="displayuploadForm">
	<%-- Release 5 changes End --%>
	<input type="hidden" value="${cancelUploadDocument}" id="cancelUploadDocumentUrl"/>
	<input type="hidden" value="${rfpDocumentRenderUrl}" id="rfpDocumentRender"/>
	<input type="hidden" value="<%=document.getFilePath()%>" name="filepath" id="filepath">
	<input type="hidden" value="<%=document.getDocCategory()%>" name="documentCategory">
	<input type="hidden" value="<%=document.getDocType()%>" name="documentType">
	<input type="hidden" value="${proposalId}" id="proposalId" name="proposalId">
	<input type="hidden" value="${filelocationUrl}" id="filelocation"/>
	<input type="hidden" value="${uploadDocumentBack1}" id="uploadDocumentBack1"/>
	<input type="hidden" id="message" name ="message"/>
	<input type="hidden" id="messageType" name ="messageType"/>
	<input type="hidden" id="next_action" name ="next_action"/>
	<div class="messagedivover" id="messagedivovertask"> </div>
			<div id="formcontainer1" class="formcontainer" style='width:800px;'>
						<div class="pad10">Please enter required Document Information, if applicable, and confirm the existing information.<br/>Note: If this is replacing an existing document, any sharing privileges will be applied to this document.</div>	
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
					       while(loIterator.hasNext()){%>
					       		<script type="text/javascript">
					       			$(".reqiredDiv").show();
					       		</script>
					       		<%DocumentPropertiesBean loDocPropsBean = (DocumentPropertiesBean) loIterator.next();
					       		
					       		if("string".equalsIgnoreCase(loDocPropsBean.getPropertyType()))
					       		{
				        		 	if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && loDocPropsBean.isIsdisabled())
					       		 	{
								       %>
								       <div class="row">
								       <span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
								       <span class="formfield"><input class= "readonly" type="text" style="width:35px" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" readonly="readonly" value= "<%=loDocPropsBean.getPropValue()%>"/>
									   <%  
						       		 }
						       		 else
						       		 {
							       	 if(loDocPropsBean.getPropDisplayName().equalsIgnoreCase("")){
							       		 	
							       		if(loDocPropsBean.getPropValue()!=null && !loDocPropsBean.getPropValue().toString().equals("")) {%>
							           		<input class= "readonly" type="text" style="width:45px" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" readonly="readonly" value= "<%=loDocPropsBean.getPropValue()%>"/>
							            <%}else{ %>
							             	<input type="text" style="width:45px" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>"/>
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
					     	<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
					        <span class="formfield"><input type="checkbox" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>"/></span>
			    		    <span class="error"></span>
			    		 </div>
			    		 <% }else if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && "int".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%>
			    		 <div class="row">
					     	<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
					        <span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>"/></span>
			    		    <span class="error"></span>
			    		 </div>
					     <% }else if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && "date".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%> 		
					   	 <div class="row">
					       	<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
					       	<span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" validate="calender" maxlength="10" <%if(loDocPropsBean.getPropValue()!=null && !loDocPropsBean.getPropValue().toString().equals("")) {%> value= "<%=loDocPropsBean.getPropValue()%>"<%}%> />
							      <img src="../framework/skins/hhsa/images/calender.png" title="Select a Date" onclick="NewCssCal('<%=loDocPropsBean.getPropertyId()%>',event,'mmddyyyy');return false;"/>
						    </span>
						    <span class="error"></span>
			    		 </div>
					    <%
					       }}
					    %>
						<div class='buttonholder'>
							<input type="button" value="Cancel" title="Cancel" name="cancel1" id="cancel1"  class="graybtutton"/>
		                	<input type="button" title="Back" name ="back1" id="back1" value="Back" class="graybtutton"/> 
		                	<input type="submit" value="Next" title="Next" name="next2" id="next2" />
						</div> 
					</div>

	</form>
</div>

