<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/uploadfile.js"></script>
<%-- Start : Changes in R5 --%>
<link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
<link rel="stylesheet" href="../css/style.css" type="text/css"></link>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<%-- End : Changes in R5 --%>
<script type="text/javascript">
var displayuploadForm;
var validate;
var againcall= false;
//on load function to perform various checks on loading of jsp
function onReady(){
		validate = true;
		displayuploadForm = document.displayuploadForm;
		  //else block is added and if block is modified as part of Release 2.7.0 defect:5616
		if(!againcall && "null" != '<%= request.getAttribute("message")%>'){
			$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
			$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagedivover").show();
			againcall = true;
			
		}else if(!againcall && "null" != '<%= session.getAttribute("message")%>'){
			$(".messagedivover").html('<%= session.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
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
		// Start Changes in R5
		// This will execute when Upload Document button is clicked
		//$(".alert-box").find('#next2').unbind("click").click(function() { 
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
			$("#displayuploadForm").validate({
				rules: {
					periodcoveredfrom: {required: true,
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
						},
					helpradio: {required: true},
					helpdesc: {required: true, 
						maxlength: 250}
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
					datelastupdated: {
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
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
						required: "<fmt:message key='REQUIRED_FIELDS'/>"
						},
					helpradio: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					helpdesc: {
						required: "<fmt:message key='REQUIRED_FIELDS'/>", 
						maxlength: "<fmt:message key='INPUT_250_CHAR'/>"}
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
	                    var data = $response.contents().find("#treeStructure");
	                    $("#tab1").empty();
			 			$("#tab2").empty();
			 			$("#tabnew").empty();
	                         if(data != null || data != ''){
	                                $("#tabnew").html(data.detach());
	                                var overlayLaunchedTemp = overlayLaunched;
									var alertboxLaunchedTemp = alertboxLaunched;
									$("#overlayedJSPContent").html($response);
									overlayLaunched = overlayLaunchedTemp;
									alertboxLaunched = alertboxLaunchedTemp;	
	                                callBackInWindow("onReady");
							}
					// Below classes added when user click Next button following inserting all information in the form.(Step 2)
					$('#step1').removeClass().css({"margin-left":"14px","padding-left":"9px"});	
					$('#step2').removeClass().addClass('default').css({"margin-left":"0px"});
					$('#step3').removeClass().addClass('active').css({"margin-left":"-20px","padding-left":""});	
				}else{
					//$("#tabnew").empty();
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
			
			//Added Method in R5
			$(".alert-box-upload").find('#back2').unbind("click").click(
					function() {
						pageGreyOut();
						var docType = $("#uploadingDocumentType").val();
						$("#uploadStep3").hide();
						if(docType=="BAFO")
                    	 $("#uploadStep3BAFO").hide();
                    	else
						 $("#treeStructure").hide();
						 $('#btnholder').hide();
						$('#step1').removeClass().addClass('default').css('margin-left', '26px');
						$('#step2').removeClass().addClass('active').css('margin-left', '-14px');
						$('#step3').removeClass().addClass('last').css('margin-left', '0px');
	                    $("#formcontainer1").show();
	                    removePageGreyOut();
						});
			}
// End Changes in R5
// Start || Added for Enhancement #6429 for Release 3.4.0
function setErrorMessage(){
	removePageGreyOut();
	$(".overlay").closeOverlay();
}
// Start || Changes done for Enhancement #6429 for Release 3.4.0
//Added code for release 5 for folder structure
/* function onReadyLocation(){
	$js("#overlaytree").jstree("destroy");
	tree($('#hdnopenTreeAjaxUpload').val(), 'overlaytree','customfolderid', '' ,'');
	 
} */
</script>
<div class="overlaycontent">
	<div id="error"></div>
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<portlet:defineObjects />
	<!-- R5 change -->
	<portlet:actionURL var="filelocationUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="submit_action" value="fileLocation" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="awardId" value="${awardId}" />
		<portlet:param name="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" />
		<portlet:param name="currentProcurementId" value="${procurementId}" />
		<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
		<portlet:param name="organizationId" value="${organizationId}" />
		<portlet:param name="staffId" value="${staffId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
		<portlet:param name="isFinancials" value="${isFinancials}" />
		<portlet:param name="contractId" value="${hdncontractId}" />
		<portlet:param name="proposalId" value='${proposalId}'/>
	</portlet:actionURL>
	<!-- R5 end -->
	<%-- Code updated for R4 Starts --%>
	<%-- View Response URL Starts --%>
	<portlet:renderURL var='viewResponse' id='viewResponse' escapeXml='false'>
		<portlet:param name="action" value="propEval"/>
		<portlet:param name="render_action" value="viewResponse"/>
		<portlet:param name="procurementId" value='${procurementId}'/>
		<portlet:param name="proposalId" value='${proposalId}'/>
		<portlet:param name="jspPath" value="evaluation/"/>
		<portlet:param name="IsProcDocsVisible" value="true"/>
	</portlet:renderURL>
	<%-- View Response URL Ends --%>
	<portlet:actionURL var='stepthreeback' id='stepthreeback' escapeXml='false'>
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="awardId" value="${awardId}" />
		<portlet:param name="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" />
		<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
		<portlet:param name="organizationId" value="${organizationId}" />
		<portlet:param name="staffId" value="${staffId}" />
		<portlet:param name="isFinancials" value="${isFinancials}" />
		<portlet:param name="contractId" value="${hdncontractId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:actionURL>
	<%-- Code updated for R4 Ends --%>
	<form action="#" method="post" name="displayuploadForm" id="displayuploadForm">
		<input type="hidden" value="${uploadingDocumentType}" id="uploadingDocumentType"/>
		<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
		<input type="hidden" value="${isFinancials}" id="isFinancials"/>
		<input type = 'hidden' value='${hdncontractId}' id='contractId' name='contractId' />
		<input type="hidden" id="asProcStatus" value="${asProcStatus}" name="asProcStatus"/>
		<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>
		<input type="hidden" value="${rfpDocumentRenderUrl}" id="rfpDocumentRender"/>
		<input type="hidden" value="${proposalDocumentRenderUrl}" id="proposalDocumentRender"/>
		<input type="hidden" value="${awardDocumentRenderUrl}" id="awardDocumentRender"/>
		<input type="hidden" value="${filelocationUrl}" id="filelocation"/>
		<%-- Code updated for R4 Starts --%>
		<input type = 'hidden' value='${viewResponse}' id='hiddenViewResponse'/>
		<%-- Code updated for R4 Ends --%>
		<%-- Code Added for R5 Start --%>
		<input type = 'hidden' value='${stepthreeback}' id='uploadback'/>
		<%-- Code Added for R5 End --%>
		<input type="hidden" value="<%=document.getFilePath()%>" name="filepath">
		<input type="hidden" value="<%=document.getDocCategory()%>" name="documentCategory">
		<input type="hidden" value="<%=document.getDocType()%>" name="documentType">
		<input type="hidden" value="${proposalId}" id="proposalId" name="proposalId">
		<input type="hidden" value="${awardId}" id="awardId" name="awardId">
		<%-- Code updated for R4 Starts --%>
		<input type="hidden" value="${evaluationPoolMappingId}" id="evaluationPoolMappingId" name="evaluationPoolMappingId">
		<input type="hidden" id="hiddenDocReference" value="${hiddendocRefSeqNo}" name="hiddenDocReference"/>
		<%-- Code updated for R4 Ends --%>
		<input type="hidden" id="replacingDocumentId" value="${replacingDocumentId}" name="replacingDocumentId"/>
		<input type="hidden" id="message" name ="message"/>
		<input type="hidden" id="messageType" name ="messageType"/>
		<input type="hidden" id="next_action" name ="next_action"/>
		<%-- Code updated for R4 Starts --%>
		<input type="hidden" value="${uploadProcess}" id="uploadProcess" name="uploadProcess"/>
		<input type="hidden" id="hiddenAddendumType" value="${isAddendum}" name="hiddenAddendumType"/>
		<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
		<input type="hidden" value="${organizationId}" id="organizationId"/>
		<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>
		<%-- Code updated for R4 Ends --%>
		<div class="messagedivover" id="messagedivover"> </div>
			<div id="formcontainer1" class="formcontainer" style='width:800px;'>
						<div class="pad10">Please enter required Document Information, if applicable, and confirm the existing information.<br/>Note: If this is replacing an existing document, any sharing privileges will be applied to this document.</div>	
						<div id="reqiredDiv" style="display:none" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
						<%-- <div class="row">
					      <span class="label">Document Category:</span>
					      <span class="formfield"><%=document.getDocCategory()%></span>
						</div> Commented as a part of release 5 --%>
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
					       	<%-- Start : Changes in R5 --%>
					       	<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
					       	<span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" validate="calender" maxlength="10" <%if(loDocPropsBean.getPropValue()!=null && !loDocPropsBean.getPropValue().toString().equals("")) {%> value= "<%=loDocPropsBean.getPropValue()%>"<%}%> />
							      <img src="../framework/skins/hhsa/images/calender.png" title="Select a Date" onclick="NewCssCal('<%=loDocPropsBean.getPropertyId()%>',event,'mmddyyyy');return false;"/>
						    </span>
						    <span class="error"></span>
			    		 </div>
					    <%
					       }}
					    %>
						<div class='buttonholder buttonholder-align' style="margin-right:55px !important">
						<div class='buttonholder'>
							<input type="button" value="Cancel" title="Cancel" name="cancel1" id="cancel1"  class="graybtutton"/>
		                	<input type="button" title="Back" name ="back1" id="back1" value="Back" class="graybtutton"/> 
		                	<input type="submit" value="Next" title="Next" name="next2" id="next2" />
						</div> 
					</div>
		</div>
		<%-- End : Changes in R5 --%>
	</form>
</div>