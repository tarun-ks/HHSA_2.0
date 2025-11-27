<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="../js/uploadfile.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<script type="text/javascript">
var formAction;
var displayForm;
var validate;
//on load function to perform various checks on loading of jsp

function onReady(){
		validate = true;
		formAction = document.displayform.action;
		displayForm = document.displayform;
		
		if("null" != '<%= request.getAttribute("message")%>'){
			$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
			$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagedivover").show();
			
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
			$("#displayform").validate({
				rules: {
					periodcoveredfrom: {required: true},
					periodcoveredto: {required: true},
					implementationstatus: {required: true},
					datelastupdated: {required: true},
					effectivedate: {required: true},
					periodcoveredfromyear: {required: true, 
						maxlength: 4, minlength: 4},
					periodcoveredtoyear: {required: true,
						maxlength: 4, minlength: 4},
					meetingdate: {required: true}
				},
				messages: {
					periodcoveredfrom: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					periodcoveredto: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					implementationstatus: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					datelastupdated: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					effectivedate: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
					periodcoveredfromyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
						maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
					periodcoveredtoyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
						maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
					meetingdate: {required: "<fmt:message key='REQUIRED_FIELDS'/>"}
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
						document.displayform.action=formAction+'&next_action=fileupload&isAjaxCall=true';
						$(document.displayform).ajaxSubmit(options);
					    $(".alert-box").hide();
						uploadGreyOut();
					}
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
						if(responsesArr[1] == "Error")
						{
							$( "#formcontainer1" ).show();
						}
						else if(responsesArr[1] == "Exception")
						{
							$(".alert-box").show();
							$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
							$(".messagedivover").addClass(responsesArr[4]);
							$(".messagedivover").show();
							removePageGreyOut();
						}
						else
						{
							window.location.href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_documentlist&_nfls=false&removeNavigator=true&action=showdocumentlist&responsemsg=M03";
						}
					},
					error:function (xhr, ajaxOptions, thrownError)
					{   
						showErrorMessagePopup();
						removePageGreyOut();
					}
			    };
			});
}
</script>

<div class="overlaycontent">
	<div id="error"></div>
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<portlet:defineObjects />
	<form action="<portlet:actionURL/>" method="post" name="displayform" id="displayform">
	<input type="hidden" value="<%=document.getFilePath()%>" name="filepath">
	<input type="hidden" value="<%=document.getDocCategory()%>" name="documentCategory">
	<input type="hidden" value="<%=document.getDocType()%>" name="documentType">
	<input type="hidden" id="message" name ="message"/>
	<input type="hidden" id="messageType" name ="messageType"/>
	<input type="hidden" id="next_action" name ="next_action"/>
	<div class="messagedivover" id="messagedivover"> </div>
	<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_PROVIDER_PAGE, request.getSession())){%>
			<div id="formcontainer1" class="formcontainer" style='width:800px;'>
			<%-- Start changes for R5 --%>
						<div class="pad10">Please enter required Document Information, if applicable, and confirm the existing information.<br/>Note: If this is replacing an existing document, any sharing privileges will be applied to this document.</div>	
			<%-- End changes for R5 --%>
						<div id="reqiredDiv" style="display:none" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
						<div class="row">
					      <span class="label">Document Category:</span>
					      <span class="formfield"><%=document.getDocCategory()%></span>
						</div>
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
					       	<span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" validate="calender" maxlength="10"/>
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
		                	<%-- Start changes for R5 --%>
		                	<input type="submit" value="Upload Document" title="Upload Document" name="next2" id="next2" />
							<%-- End changes for R5 --%>
		                	<input type="hidden" name="OldDocumentIdReq" id ="OldDocumentIdReq" value='${OldDocumentIdReq}' />	
						</div> 
					</div>        
			<% } else {%>
		   <h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		   <%} %> 
	</form>
</div>

