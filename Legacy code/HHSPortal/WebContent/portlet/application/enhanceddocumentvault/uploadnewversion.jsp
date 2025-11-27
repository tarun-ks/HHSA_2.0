<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@page errorPage="/error/errorpage.jsp" %>
<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="../js/enhanceduploadfile.js"></script>
<script type="text/javascript">
//on load function to perform various checks on loading of jsp
function onReady(){
		if("null" != '<%= request.getAttribute("message")%>'){
			$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
			$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagedivover").show();
		}
		if("Sample" == '<%= request.getAttribute("category")%>'){
			$('#sampleCategoryDiv').show();
			$('#sampleTypeDiv').show();
		}
		// This will execute when Next button is clicked from upload new version
		$(".alert-box").find('#next3').unbind("click").click(function() { 
			$("#uploadnewform").validate({
				rules: {
					uploadnewversion: {required: true}
				},
				messages: {
					uploadnewversion: {required:"<fmt:message key='REQUIRED_FIELDS'/>"}
				},
				submitHandler: function(form){
					document.uploadnewform.action=$("#uploadnewform").attr('action')+'&next_action=fileinformation&documentId=${document.documentId}';
					$(document.uploadnewform).ajaxSubmit(options);
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
								}
							var overlayLaunchedTemp = overlayLaunched;
							var alertboxLaunchedTemp = alertboxLaunched;
							$("#overlayedJSPContent").html($response);
							overlayLaunched = overlayLaunchedTemp;
							alertboxLaunched = alertboxLaunchedTemp;
							callBackInWindow("onReady");	
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
		});
}
</script>

<portlet:defineObjects />
<div class="overlaycontent">
	<form action="<portlet:actionURL/>" enctype="multipart/form-data" method="post" name="uploadnewform" id="uploadnewform">
		<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
			<div class="formcontainer">
			<div class="messagedivover" id="messagedivover"> </div>
			<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S032_CITY_PAGE, request.getSession())){%>
					<div class="pad10">Select a document type, then browse your computer for the file to upload.</div>
					<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
					<div class="row">
				      <span class="label">Document Category:</span>
				      <span class="formfield"><%=document.getDocCategory()%></span>
					</div>
					<div class="row" id="sampleCategoryDiv" style="display:none">
				      <span class="label">Sample Document Category:</span>
				      <span class="formfield"><%=document.getSampleCategory()%></span>
					</div>
					<div class="row" id="sampleTypeDiv" style="display:none">
				      <span class="label">Sample Document Type:</span>
				      <span class="formfield"><%=document.getSampleType()%></span>
					</div>
					<div class="row">
				      <span class="label">Document Name:</span>
				      <span class="formfield"><%=document.getDocName()%></span>
					</div>
					<div class="row">
						<span class="label"><label class="required">*</label>Select the file to upload:</span>
						<span class="formfield"><input type="file" name="uploadnewversion"/></span>
						<span class="error docnameError"></span>
					</div> 
    				<div class='buttonholder'>
						<input type="button" class="graybtutton" value="Cancel" title="Cancel" name="cancel" id="cancelnewversion" />
						<input type="submit" value="Next" title="Next" name="next3" id="next3"  />
					</div>
			<% } else {%>
		    	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		    <%} %>
			</div>  
 			<input type="hidden" name="hiddenDocCategory" value='<%=document.getDocCategory()%>' />	
			<input type="hidden" name="hiddenDocType" value='<%=document.getDocType()%>'/>
			<input type="hidden" name="hiddenFilterModifiedFrom" value='<%=document.getFilterModifiedFrom()%>' />
			<input type="hidden" name="hiddenFilterModifiedTo" value='<%=document.getFilterModifiedTo()%>' />
			<input type="hidden" name="hiddenFilterProviderId" value='<%=document.getFilterProviderId()%>' />
			<input type="hidden" name="hiddenFilterNYCAgency" value="<%=document.getFilterNYCAgency()%>"/>
			<input type="hidden" name="hiddenDocShareStatus" value='<%=document.getDocSharedStatus()%>' />	
			<input type="hidden" name="hiddenSampleCategory" value='<%=document.getFilterSampleCategory()%>'/>	
			<input type="hidden" name="hiddenSampleType" value='<%=document.getFilterSampleType()%>'/>      
	</form>
</div>


