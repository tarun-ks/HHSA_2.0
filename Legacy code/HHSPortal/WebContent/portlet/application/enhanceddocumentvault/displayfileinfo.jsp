<div id="uploadStep2">
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant, com.nyc.hhs.constants.ApplicationConstants" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="../js/enhanceduploadfile.js"></script>
<script type="text/javascript" src="../resources/js/applicationSummary.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<script type="text/javascript">
var formAction;
var displayForm;
var validate;
//on load function to perform various checks on loading of jsp

function onReadyStep2(){
		validate = true;
		formAction = document.displayform.action;
		displayForm = document.displayform;
		$('.editable').off("keydown keyup keypress");
		if("null" != '<%= request.getAttribute("message")%>')
 		{
			$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivoverError', this)\" />");
			$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagedivover").show();
			
		}
		if('<%= request.getAttribute("lbFlag")%>' == "true")
		{
			$("#next2").attr("disabled", "disabled");
		}
		if('<%=ApplicationConstants.DOCUMENT_TYPE_HELP%>' == '<%=request.getAttribute("category")%>'){
			$('#helpcategory').show();
			$('#helpradio').show();
			$('textArea').removeClass('ignoreFields');
			$('#helpdesc').show();
		}
		if('<%=ApplicationConstants.DOC_SAMPLE%>' == '<%=request.getAttribute("category")%>'){
			$('#sampleCategory').show();
			$('#sampleType').show();
		}
		if(<%=request.getAttribute("type")%> != 'null'){
			$('#doctype').show();
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
		// This will execute when Upload Document button is clicked
		$(".alert-box").find('#next2').unbind("click").click(function() { 
		$("#displayform").validate({
                ignore: ".ignoreFields",
				rules: {
					helpcat: {required: true},
					help: {required: true},
					periodcoveredfrom: {required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true
						},
					periodcoveredto: {required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true
					},
					implementationstatus: {required: true},
					datelastupdated: {required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true
						},
					effectivedate: {required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true
						},
					periodcoveredfromyear: {required: true, 
						maxlength: 4, minlength: 4},
					periodcoveredtoyear: {required: true,
						maxlength: 4, minlength: 4},
					meetingdate: {required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true
						} 
				},
				messages: {
					helpcat: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					help: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					docDesc: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					implementationstatus: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					datelastupdated: {minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>"
						},
					effectivedate: {minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>"
						},
					periodcoveredfromyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
						maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
					periodcoveredtoyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
						maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
					meetingdate: {
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>"
						},
					periodcoveredfrom: {
							required: "<fmt:message key='REQUIRED_FIELDS'/>",
							minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
							maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
							DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>"
							},
					periodcoveredto: {
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>"
						} // Release 5 ends
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
					if($('#documentType').val() == 'Help' && $.trim($('#docDesc').val()).length == 0){
						$('.errorForHelp').html('! This field is required.');
						$('.errorForHelp').show();
						isValid = false;
					}
					else
					{
						$('.errorForHelp').hide();
					}
					
					if(isValid){
						document.displayform.action=formAction+'&submit_action=getFolderLocation&isAjaxCall=true&action=enhanceddocumentvault';
						$(document.displayform).ajaxSubmit(options);
						pageGreyOut();
					}
				},
				errorPlacement: function(error, element) {
					error.appendTo(element.parent().parent().find("span.error"));
				      if($.trim($('#docDesc').val()).length == 0){
							$('.errorForHelp').html('! This field is required.');
						}
				}
			});
			
			    var options = 
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
				 			 $("#tab2").empty();
				 			$("#tabnew").empty();
		                         if(data != null || data != ''){
		                                $("#tabnew").html(data.detach());
		                                var overlayLaunchedTemp = overlayLaunched;
										var alertboxLaunchedTemp = alertboxLaunched;
										$("#overlayedJSPContent").html($response);
										overlayLaunched = overlayLaunchedTemp;
										alertboxLaunched = alertboxLaunchedTemp;	
		                                callBackInWindow("onReadyStep3");
								}
						// Below classes added when user click Next button following inserting all information in the form.(Step 2)
						$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
						$('#step1').removeClass().css({"margin-left":"14px","padding-left":"9px"});	
						$('#step2').removeClass().addClass('default').css({"margin-left":"0px"});
						$('#step3').removeClass().addClass('active').css({"margin-left":"-20px","padding-left":""});	
					}else{
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
			});
        $("#displayform").find("input:hidden,textarea:hidden,select:hidden").addClass("ignoreFields");
}
</script>

<div class="overlaycontent">
	<div id="error"></div>
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<portlet:defineObjects />
	<form action="<portlet:actionURL/>" method="post" name="displayform" id="displayform">
	<input type="hidden" value="<%=document.getFilePath()%>" name="filepath">
	<input type="hidden" value="<%=document.getDocCategory()%>" name="documentCategory">
	<input type="hidden" value="<%=document.getDocType()%>" name="documentType" id="documentType">
	<input type="hidden" value="<%=document.getSampleCategory()%>" name="sampledoccategory">
	<input type="hidden" value="<%=document.getSampleType()%>" name="sampledoctype">
	<input type="hidden" value="<%=request.getAttribute("from_upload_version")%>" name="callFrom">
	<input type="hidden" id="message" name ="message"/>
	<input type="hidden" id="messageType" name ="messageType"/>
	<input type="hidden" id="next_action" name ="next_action"/>
	<div class="messagedivover" id="messagedivoverError"> </div>
			<div id="formcontainer1" class="formcontainer" style='width:650px;'>
						<div class="pad10">Please enter required Document Information, if applicable, and confirm the existing information. <br/>Note: If this is replacing an existing document, any sharing privileges will be applied to this document.</div>	
						<div id="reqiredDiv" style="display:none" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
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
					    <% 
					    if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_PROVIDER_PAGE, request.getSession())
					    		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HE_N01_SECTION_5, request.getSession())){
					    List<DocumentPropertiesBean> docProps = document.getDocumentProperties();
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
					       	<!-- Added value of the formfield and null check on it -->
					       	<span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" validate="calender" maxlength="10" <%if(loDocPropsBean.getPropValue()!=null && !loDocPropsBean.getPropValue().toString().equals("")) {%> value= "<%=loDocPropsBean.getPropValue()%>"<%} %>/>
							      <img src="../framework/skins/hhsa/images/calender.png" title="Select a Date" onclick="NewCssCal('<%=loDocPropsBean.getPropertyId()%>',event,'mmddyyyy');return false;"/>
						    </span>
						    <span class="error"></span>
			    		 </div>
					    <%
					       }}}
					    else if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_CITY_PAGE, request.getSession())){
					    %>			
				<div class="row" id="sampleType" style="display:none">
					<span class="label">Sample Document Type:</span>
					<span class="formfield"><%=document.getSampleType()%></span>
				</div>
					    <div class="row" id="helpcategory" style="display:none">
					<span class="label"><label class="required">*</label>Help Category:</span>
					<span class="formfield">
						<select id = "helpcat" name="helpcat">
							<c:forEach var="helpcategory" items="${document.helpCategoryList}" >
								<option value="<c:out value="${helpcategory}"/>" <c:if test="${ helpcategory eq document.helpCategory}"> selected </c:if>  ><c:out value="${helpcategory}"/></option>
							</c:forEach>
						</select>
					</span>
					<span class="error"></span>
				</div>
				
				<div class='row' id="helpradio" style="display:none">
					<span class='label' style='height: 40px;'> <label class="required">*</label>Display this document on the page specific help page?:</span>
					<span class='formfield'>
						<input type="radio" name="help" value="yes" id="rdoyes" ${(document.helpRadioButton eq 'yes') ? 'checked' : ''}/><label for="rdoyes">Yes</label><br>
						<input type="radio" name="help" value="no" id='rdono'  ${(document.helpRadioButton eq 'no') ? 'checked' : ''}/><label for='rdono' >No</label><br>
					</span>
					<span class="error"></span>
				</div>
				
				<div class="row" id="helpdesc" style="display:none">
	     			<span class="label" style='height: 114px;'><label class="required">*</label>Document Description:</span>
	      			<span class="formfield"><textarea rows="7" cols="30" name="docDesc" id="docDesc" onkeyup="setMaxLength(this,250)" onkeypress="setMaxLength(this,250)" error="errorForHelp" style="resize:none;width:246px;height:90px;">${document.helpDocDesc}</textarea></span>
	      			<span id ="errorForHelp" class="error errorForHelp"></span>
    			</div>
    			
    			<% } else {%>
		   <h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		   <%} %> 
						<div class='buttonholder buttonholder-align1'>
							<input type="button" value="Cancel" title="Cancel" name="cancel1" id="cancel1"  class="graybtutton"/>
		                	<input type="button" title="Back" name ="back1" id="back1" value="Back" class="graybtutton"/> 
		                	<input type="submit" value="Next" title="Next" name="next2" id="next2" />
		                	<input type="hidden" name="OldDocumentIdReq" id ="OldDocumentIdReq" value='${OldDocumentIdReq}' />	
						</div> 
					</div>        
	</form>
</div>
</div>
