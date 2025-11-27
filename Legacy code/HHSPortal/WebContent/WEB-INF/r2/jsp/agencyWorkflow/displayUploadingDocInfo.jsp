<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/uploadfile.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<!--Start Added in R5 -->
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
<link rel="stylesheet" href="../css/style.css" type="text/css"></link>
<!--End Added in R5 -->
<script type="text/javascript">
var formAction;
var displayuploadForm;
var validate;
var againcall= false;
//on load function to perform various checks on loading of jsp
function onReady(){
		validate = true;
		formAction = document.displayuploadForm.action;
		displayuploadForm = document.displayuploadForm;
		//else block is added as part of Release 2.6.0 defect:5612	
		if("null" != '<%= request.getAttribute("message")%>'){
			$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" onclick=\"showMe('messagedivover', this)\" />");
			$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagedivover").show();
			
		}else if(!againcall && "null" != '<%= session.getAttribute("message")%>'){
			$(".messagedivover").html('<%= session.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
			$(".messagedivover").addClass('<%= session.getAttribute("messageType")%>');
			$(".messagedivover").show();
			<%session.removeAttribute("message");%>
			<%session.removeAttribute("messageType");%>
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
//Start Added in R5
		$('#periodcoveredfromyear').numeric(); 
		$("input[name='periodcoveredfromyear']").fieldFormatter("XXXX");
		$('#periodcoveredtoyear').numeric();
		$("input[name='periodcoveredtoyear']").fieldFormatter("XXXX");
		// This will execute when Upload Document button is clicked
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
		// $(".alert-box").find('#next2').unbind("click").click(function() {
			$("#displayuploadForm").validate({
				rules: {
					periodcoveredfrom: {
						required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						calenderRestrictFutureDate : true,
						DateRange : new Array("01/01/1800", today)
					},
					periodcoveredto: {
						required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						calenderRestrictFutureDate : true,
						DateRange : new Array("01/01/1800", today),
						DateToFrom: new Array("periodcoveredfrom",false)
						},
					implementationstatus: {
						required: true
						},
					datelastupdated: {
						required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						calenderRestrictFutureDate : true,
						DateRange : new Array("01/01/1800", today)
						},
					effectivedate: {
						required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						calenderRestrictFutureDate : true,
						DateRange : new Array("01/01/1800", today)
						},
					periodcoveredfromyear: {
						required: true, 
						maxlength: 4, 
						minlength: 4
						},
					periodcoveredtoyear: {
						required: true,
						maxlength: 4, 
						minlength: 4
						},
					meetingdate: {
						required: true,
						minlength : 10,
						maxlength : 10,
						DateFormat : true,
						calenderRestrictFutureDate : true,
						DateRange : new Array("01/01/1800", today)
						},
					helpradio: {required: true},
					helpdesc: {required: true, 
							maxlength: 250}
				},
				messages: {
					periodcoveredfrom: {
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
						calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"},
					periodcoveredto: {
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
						DateToFrom : "<fmt:message key='M60'/>",
						calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
						},
					implementationstatus: {
						required: "<fmt:message key='REQUIRED_FIELDS'/>"
						},
					datelastupdated: {
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
						calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
						},
					effectivedate: {
						minlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						maxlength: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
						calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
						},
					periodcoveredfromyear: {
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
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
						DateFormat: "<fmt:message key='REQUIRED_VALID_DATE'/>",
						required: "<fmt:message key='REQUIRED_FIELDS'/>",
						DateRange: "<fmt:message key='REQUIRED_VALID_DATE2'/>",
						calenderRestrictFutureDate:"! Invalid Date. Please enter a date in the past"
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
						document.displayuploadForm.action= $("#filelocation").val();
						$(document.displayuploadForm).ajaxSubmit(options);
						pageGreyOut();
					}
				},
				errorPlacement: function(error, element) {
				      error.appendTo(element.parent().parent().find("span.error"));
				}
			});
			
		//});
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
	//End Added R5
function setErrorMessage(){
	removePageGreyOut();
	$(".overlay").closeOverlay();
}
}
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
	<portlet:actionURL var="uploadDocumentUrl" escapeXml="false">
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<portlet:param name="workflowId" value="${workflowId}" />
	</portlet:actionURL>
	<portlet:actionURL var='stepthreeback' id='stepthreeback' escapeXml='false'>
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<portlet:param name="workflowId" value="${workflowId}" />
	</portlet:actionURL>
	<portlet:renderURL var="awardDocumentRenderUrl" escapeXml="false">
		<portlet:param name="render_action" value="configureAwardDocumentTaskDetails" />	
	</portlet:renderURL>
	<!--Start Added in R5 -->
	<portlet:actionURL var="filelocationUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="submit_action" value="fileLocation" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="workflowId" value="${workflowId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<portlet:param name="awardId" value="${awardId}" />
		<portlet:param name="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" />
		<portlet:param name="currentProcurementId" value="${procurementId}" />
		<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
		<portlet:param name="organizationId" value="${organizationId}" />
		<portlet:param name="staffId" value="${staffId}" />
		<portlet:param name="contractId" value="${hdncontractId}" />
		<portlet:param name="proposalId" value='${proposalId}'/>
	</portlet:actionURL>
	<!--End Added in R5 -->
	<form action="#" method="post" name="displayuploadForm" id="displayuploadForm">
	<input type="hidden" value="${document.filePath}" name="filepath" />
	<input type="hidden" value="${document.docCategory}" name="documentCategory" />
	<input type="hidden" value="${document.docType}" name="documentType" />
	<input type = "hidden" value="${stepthreeback}" id="uploadback"/>
	<input type="hidden" value="${filelocationUrl}" id="filelocation"/>
	<input type="hidden" id="message" name ="message"/>
	<input type="hidden" id="messageType" name ="messageType"/>
	<input type="hidden" id="next_action" name ="next_action"/>
	<input type="hidden" value="${awardDocumentRenderUrl}" id="awardDocumentRender"/>
	<div class="messagedivover" id="messagedivover"> </div>
			<div id="formcontainer1" class="formcontainer" style='width:800px;'>
						<div class="pad10">Please enter required Document Information, if applicable, and confirm the existing information.<br/>Note: If this is replacing an existing document, any sharing privileges will be applied to this document.</div>	
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
							      <img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('<%=loDocPropsBean.getPropertyId()%>',event,'mmddyyyy');return false;"/>
						    </span>
						    <span class="error"></span>
			    		 </div>
					    <%
					       }}
					    %>
						<div class='buttonholder'>
							<input type="button" value="Cancel" name="cancel1" id="cancelTaskDocument1"  class="graybtutton"/>
		                	<input type="button" name ="back1" id="back1" value="Back" class="graybtutton"/> 
		                	<input type="submit" value="Next" name="next2" id="next2" />
						</div> 
					</div>  
	</form>
	<!--Start Added in R5 -->
	<portlet:resourceURL var="openTreeAjax" id="openTreeAjax" escapeXml="false"></portlet:resourceURL>
	<input type="hidden" name="hdnopenTreeAjaxUpload" id ="hdnopenTreeAjaxUpload" value='${openTreeAjax}' />
<!--End Added in R5 -->
</div>

