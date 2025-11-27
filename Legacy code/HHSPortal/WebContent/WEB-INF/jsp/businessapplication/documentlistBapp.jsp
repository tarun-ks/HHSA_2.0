<!-- This page is displayed when a user click on save and next button on  basic,filing,board etc question screen.
It will display list of corresponding documents.Here a user can upload or select a document from the vault.-->
<%@page errorPage="/error/errorpage.jsp"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.nyc.hhs.util.PortalUtil"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<portlet:defineObjects/>
<%-- Start : Changes in R5 --%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/jstree.min.js"></script> 
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/style.css" type="text/css"></link>
<%-- End : Changes in R5 --%>
<style type="text/css">
.commentHidden{
		display:none;
	}
.messagedivNycMsg{
		display:none;
	}
.ui-widget{
	font-family: Verdana !important;
	font-size: 12px !important;
}
.alert-box, .alert-box-link-to-vault{
	background: none repeat scroll 0 0 #FFFFFF;
    display: none;
    /*position: fixed;
    top: 12%;
    left:14%;
    width: 72%;*/
    z-index: 1001;
}
.alert-box .sub-started a, .alert-box-link-to-vault .sub-started a{
	color:#fff;
}
.alert-box .sub-notstarted, .alert-box-link-to-vault .sub-notstarted{
	background-image:none;
	color:#fff;
}

/* alert box style is overrided to fix defect 5592 as part of release 2.7.0*/
.alert-box{

        position:fixed !important;

        top:25% !important

}
.formcontainer{
	position: relative;
}
#tab1 #main-wrapper{
	width: 880px;
}
#tab1 #main-wrapper .bodycontainer{
	background: none;
	padding: 0;
	width: 100%;
}
#tab1 #main-wrapper .bodycontainer br{
	display: none;
}
h2{
    		width: 96% !important;
    	}
</style>
<script type="text/javascript"> 
//Method while rendering.
	var documentMyFormUploadAction;	
	$(document).ready(function() {
		//Start : Changes in R5
		<%
		if(renderRequest.getAttribute("bappReadOnlyUser") !=null && ((String)renderRequest.getAttribute("bappReadOnlyUser")).equalsIgnoreCase("true")){%>
		$('.viewOrRemDoc').prop("disabled",true);
		<%}%>
		//End : Changes in R5
		documentMyFormUploadAction = document.myformupload.action;
    	$("select").change(function() {
    		var docId=$(this).attr("docId");
    		var formName=$(this).attr("formName");
    		var formVersion=$(this).attr("formVersion");
    		var orgId=$(this).attr("orgId");
    		var docType=$(this).attr("docType");
    		$('input[name="docType"]').val(docType);
    		var appId=$(this).attr("appId");
    		var docCat=$(this).attr("docCat");
    		var userId=$(this).attr("userId");
    		var formId=$(this).attr("formId");
    		var docName=$(this).attr("docName");
    		var serviceAppId=$(this).attr("serviceAppId");
    		var sectionId=$(this).attr("sectionId");
    		var functionName=$(this).attr("functionName");
    		var entityId=$(this).attr("entityId");
    		//Start : Changes in R5
    		$('#docTypeHidden').val(docType);
    		$('#docCatHidden').val(docCat);
    		 var str = "";
    		 if(functionName == 'openDocument' )
    			 {
    			 openDocument(docId,formName,formVersion,orgId,docType,appId,docCat,this,userId,formId,docName);}
    		 else if(functionName == 'uploadDocument' )
    			 {
    			 uploadDocument(formName,formVersion,docType,docCat,userId,formId,this);}
    		 else if(functionName == 'uploadDocumentServiceSummary')
    			 {
    			 
    			 uploadDocumentServiceSummary(docType,docCat,serviceAppId,sectionId,entityId,this);}
    		 else if (functionName == 'openDocumentServiceSummary')
    		 {
    			 openDocumentServiceSummary(docId,docType,docCat,serviceAppId,sectionId,this,userId,formId,docName,entityId);}
			 //End : Changes in R5
			 var options = 
    					{	
					    	success: function(responseText, statusText, xhr ) 
							{
							 	var $response=$(responseText);
	                            var data = $response.contents().find(".overlaycontent");
	                            	$("#tab1").empty();
						 			$("#tab2").empty();
						 			$("#tabnew").empty();
	                            if(data != null || data != ''){
	                                  $("#tab1").html(data.detach());
	                                  var overlayLaunchedTemp = overlayLaunched;
									var alertboxLaunchedTemp = alertboxLaunched;
									$("#overlayedJSPContent").html($response);
									overlayLaunched = overlayLaunchedTemp;
									alertboxLaunched = alertboxLaunchedTemp;
									$(".overlay").launchOverlay($(".alert-box-upload"), $(".exit-panel"), "800px", null, "onReady");
								}
								$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
								$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
								removePageGreyOut();
							},
							error:function (xhr, ajaxOptions, thrownError)
							{                     
								showErrorMessagePopup();
								removePageGreyOut();
							}
					    };
				var options1 = 
    					{	
					    	success: function(responseText, statusText, xhr ) 
							{
							 	$("#linktovault").empty();
								$("#linktovault").html(responseText);
								var $response=$(responseText);
	                            var data = $response.contents().find(".overlaycontent");
	                            	$("#linktovault").empty();
	                            if(data != null || data != ''){
	                                  $("#linktovault").html(data.detach());
	                                  var overlayLaunchedTemp = overlayLaunched;
									var alertboxLaunchedTemp = alertboxLaunched;
									$("#overlayedJSPContent").html($response);
									overlayLaunched = overlayLaunchedTemp;
									alertboxLaunched = alertboxLaunchedTemp;
									$(".overlay").launchOverlay($(".alert-box-link-to-vault"), $(".exit-panel"), "850px", "635px", "onReady");
								}
								removePageGreyOut();
							},
							error:function (xhr, ajaxOptions, thrownError)
							{                     
								showErrorMessagePopup();
								removePageGreyOut();
							}
					    };	
			   $("select option:selected").each(function () {
				   str= $(this).text();
					//Modified for 3.1.0,Enhancement #6021; Modifying + sign in URl to %2B to save it from getting lost.	
					$(this.form).attr('action', $(this.form).attr('action').replaceAll('+', '%2B'));
				   	if(str == 'Upload Document'){
				   		pageGreyOut();
					 	$(this.form).ajaxSubmit(options);
					 	return false;
					}
					if(str == 'Select Document from Vault'){
						pageGreyOut();
					 	$(this.form).ajaxSubmit(options1);
						return false;
					}
				});			

		});		
   		$("a.exit-panel").click(function(){	
   			$(".overlay").closeOverlay();				
			$('.documentterms').find('option:first').attr('selected', 'selected');
			$('.viewOrRemDoc').find('option:first').attr('selected', 'selected');
		});		
	
		if("null" != '<%= request.getAttribute("message")%>'&& '<%= request.getAttribute("messagetypeNyc")%>' !="yes"){
			$(".messagediv").html('${message}'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			$(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
			$(".messagediv").show();
			<%request.removeAttribute("message");%>
		}
		
		if("null" != '<%= request.getAttribute("messageNyc")%>' && '<%= request.getAttribute("messagetypeNyc")%>' =="yes"){
			$(".messagedivNyc").html('${messageNyc}');
			$(".messagedivNyc").addClass('passed');
			$(".messagedivNyc").show();
			$(".messagedivNycMsg").show();
			<%request.removeAttribute("message");%>
		}
});
//Method executed when any document is uploaded for a Business application.
function openDocument(documentId,formName,formVersion,asOrgId,docType,asAppId,asDocCat,selectElement,userId,formId,documentName){
	var index = selectElement.selectedIndex;	
	var value = selectElement.options[selectElement.selectedIndex].value;
	if(value == 'View Document'){
		viewDocument(documentId, documentName);
		selectElement.selectedIndex = "";
	}
	if(value == 'Remove Document'){
		$('<div id="dialogBox"></div>').appendTo('body')
		.html('<div><h6>Are you sure you want to remove this document from your organization&#39;s application? <br />This will not delete the document from your vault. </h6></div>')
		.dialog({
			modal: true, title: 'Remove Document from Application', zIndex: 10000, autoOpen: true,
			width: 'auto', modal: true, resizable: false,draggable:false,
			buttons: {
				Yes: function () {
					//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
					document.myformupload.action = documentMyFormUploadAction +'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&documentId='+documentId+'&asAppId='+asAppId+'&asDocId='+documentId+'&asDocCat='+asDocCat+'&formName='+formName+'&formVersion='+formVersion+'&asOrgId='+asOrgId+'&next_action=removeDocFromApplication&section='+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'${elementId}';
					document.myformupload.submit();
					$(this).dialog("close");
				},
				No: function () {
					$(selectElement).find("option").eq(0).attr('selected', 'selected');
					$(this).dialog("close");
				}
			},
			close: function (event, ui) {
				$(this).remove();
			}
		});
	}
	if(value == 'Upload Document'){
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		document.myformupload.action = documentMyFormUploadAction+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&formName='+formName+'&formVersion='+formVersion+'&formId='+formId+'&docCategory='+asDocCat+'&userId='+userId+'&removeNavigator=true&removeMenu=true&next_action=documentupload&section='+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'${elementId}';
	}
	if(value == 'Select Document from Vault'){
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		document.myformupload.action = documentMyFormUploadAction+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&docCategory='+asDocCat+'&userId='+userId+'&formName='+formName+'&formVersion='+formVersion+'&formId='+formId+'&removeNavigator=true&removeMenu=true&next_action=selectDocFromVault'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'${elementId}'+"&selectVault=true&selectAll=true&selectDocForRelease=BusinessApp";
	}
	if(value == 'View Document Information'){
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		document.myformupload.action = documentMyFormUploadAction+'&docType='+docType+'&asDocId='+documentId+'&asDocCat='+asDocCat+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&documentId='+documentId+'&asAppId='+asAppId+'&asDocId='+documentId+'&asDocCat='+asDocCat+'&formName='+formName+'&formVersion='+formVersion+'&asOrgId='+asOrgId+'&next_action=viewDocumentInfo&section='+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'${elementId}';
		// Release 5
			var options = 
		    			{	
						   	success: function(responseText, statusText, xhr ) 
							{
						   		if(responseText.indexOf("<head><meta http-equiv")!=-1 && responseText.indexOf("<head><meta http-equiv")<300){
						    	 	var responseText1=responseText.slice(responseText.indexOf('<head><meta http-equiv'),(responseText.indexOf('</head>')+7));
							   		responseText = responseText.replace(responseText1,"");
						    	}
								var $response=$(responseText);
		                        var data = $response.contents().find(".overlaycontent");
		                        $("#viewDocumentProperties").html(data.detach());
		                        $("#overlayedJSPContent").html($response);
								$(".overlay").launchOverlay($(".alert-box-viewDocumentProperties"), $(".exit-panel.upload-exit"), "650px", null, "onReady");
								var a=$('.documentLocationPath').text().trim();
								a=a.replace(/\\/g, "&#x200b;\\&#x200b;");
								b='<div style="width:50ch;" ></div>';
								$('.documentLocationPath').html(b);
								$('.documentLocationPath div').html(a);
								removePageGreyOut();
							},
							error:function (xhr, ajaxOptions, thrownError)
							{                     
								showErrorMessagePopup();
								removePageGreyOut();
							}
						};
			pageGreyOut();
			$(document.myformupload).ajaxSubmit(options);
		
		//End
	}
}
//next button action.
function nextAction(nextaction){
	document.myformupload.action=documentMyFormUploadAction+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&next_action='+nextaction+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'${elementId}';
	document.myformupload.submit();
	}
//Method executed when any document is not uploaded for a Business application.
function uploadDocument(formName,formVersion,docType,docCategory,userId,formId,selectElement)
{
	pageGreyOut();
	var value = selectElement.selectedIndex;
	if(value == 1){
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		document.myformupload.action = documentMyFormUploadAction+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&formName='+formName+'&formVersion='+formVersion+'&formId='+formId+'&docCategory='+docCategory+'&userId='+userId+'&removeNavigator=true&removeMenu=true&next_action=documentupload&section='+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'${elementId}';
	}
	if(value == 2){
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		document.myformupload.action = documentMyFormUploadAction+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&docCategory='+docCategory+'&userId='+userId+'&formName='+formName+'&formVersion='+formVersion+'&formId='+formId+'&removeNavigator=true&removeMenu=true&next_action=selectDocFromVault'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'${elementId}'+"&selectVault=true&selectAll=true&selectDocForRelease=BusinessApp";
	}
}
//Method executed when any document is not uploaded for a Business application service.
function uploadDocumentServiceSummary(docType,docCategory,serviceAppID,sectionId,entityId,selectElement)
{
	var value = selectElement.selectedIndex;
	if(value == 1){
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		document.myformupload.action = documentMyFormUploadAction+'&docCategory='+docCategory+'&entityId='+entityId+'&serviceAppID='+serviceAppID+'&sectionId='+sectionId+'&removeNavigator=true&removeMenu=true&next_action=documentupload&section='+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&elementId="+'${elementId}';
	}
	if(value == 2){
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		document.myformupload.action = documentMyFormUploadAction+'&docCategory='+docCategory+'&entityId='+entityId+'&sectionId='+sectionId+'&serviceAppID='+serviceAppID+'&removeNavigator=true&removeMenu=true&next_action=selectDocFromVault'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&elementId="+'${elementId}'+"&selectVault=true&selectAll=true&selectDocForRelease=BusinessApp";
	}
}
//Method executed when any document is uploaded for a Business application service.
function openDocumentServiceSummary(documentId,docType,docCategory,serviceAppID,sectionId,selectElement,userId,formId, documentName,entityId){

	var index = selectElement.selectedIndex;	
	var value = selectElement.options[selectElement.selectedIndex].value;
	if(value == 'View Document'){
		selectElement.selectedIndex = 0;
		viewDocument(documentId, documentName);
		
	}
	if(value == 'Remove Document'){
		$('<div id="dialogBox"></div>').appendTo('body')
		.html('<div><h6>Are you sure you want to remove this document from your organization&#39;s application? <br />This will not delete the document from your vault. </h6></div>')
		.dialog({
			modal: true, title: 'Remove Document from Services', zIndex: 10000, autoOpen: true,
			width: 'auto', modal: true, resizable: false,draggable:false,
			buttons: {
				Yes: function () {
					//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
					document.myformupload.action = documentMyFormUploadAction+'&asDocId='+documentId+'&asDocCat='+docCategory+'&sectionId='+sectionId+'&entityId='+entityId+'&serviceAppID='+serviceAppID+'&next_action=removeDocFromApplication'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&elementId="+'${elementId}';
					document.myformupload.submit();
					$(this).dialog("close");
				},
				No: function () {
					$(selectElement).find("option").eq(0).attr('selected', 'selected');
					$(this).dialog("close");
				}
			},
			close: function (event, ui) {
				$(this).remove();
			}
		});
	}
	if(value == 'Upload Document'){
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		document.myformupload.action = documentMyFormUploadAction+'&docCategory='+docCategory+'&serviceAppID='+serviceAppID+'&sectionId='+sectionId+'&entityId='+entityId+'&removeNavigator=true&removeMenu=true&next_action=documentupload&section='+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&elementId="+'${elementId}';
	}
	if(value == 'Select Document from Vault'){
		
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		//Added selectVault and selectDocForRelease in URL for Release 5- selectVault
		document.myformupload.action = documentMyFormUploadAction+'&docCategory='+docCategory+'&sectionId='+sectionId+'&entityId='+entityId+'&serviceAppID='+serviceAppID+'&removeNavigator=true&removeMenu=true&next_action=selectDocFromVault'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&elementId="+'${elementId}'+"&selectVault=true&selectAll=true&selectDocForRelease=BusinessApp";
	}
	if(value == 'View Document Information'){
		//Modified for 3.1.0,Enhancement #6021; Removing docType from Action URL
		document.myformupload.action = documentMyFormUploadAction+'&docType='+docType+'&asDocId='+documentId+'&asDocCat='+docCategory+'&sectionId='+sectionId+'&documentId='+documentId+'&serviceAppID='+serviceAppID+'&next_action=viewDocumentInfo'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&elementId="+'${elementId}';
		// Release 5
			var options = 
		    			{	
						   	success: function(responseText, statusText, xhr ) 
							{
						   		if(responseText.indexOf("<head><meta http-equiv")!=-1 && responseText.indexOf("<head><meta http-equiv")<300){
						    	 	var responseText1=responseText.slice(responseText.indexOf('<head><meta http-equiv'),(responseText.indexOf('</head>')+7));
							   		responseText = responseText.replace(responseText1,"");
						    	}
								var $response=$(responseText);
		                        var data = $response.contents().find(".overlaycontent");
		                        $("#viewDocumentProperties").html(data.detach());
		                        $("#overlayedJSPContent").html($response);
								$(".overlay").launchOverlay($(".alert-box-viewDocumentProperties"), $(".exit-panel.upload-exit"), "650px", null, "onReady");
								var a=$('.documentLocationPath').text().trim();
								a=a.replace(/\\/g, "&#x200b;\\&#x200b;");
								b='<div style="width:50ch;" ></div>';
								$('.documentLocationPath').html(b);
								$('.documentLocationPath div').html(a);
								removePageGreyOut();
							},
							error:function (xhr, ajaxOptions, thrownError)
							{                     
								showErrorMessagePopup();
								removePageGreyOut();
							}
						};
			pageGreyOut();
			$(document.myformupload).ajaxSubmit(options);
		
		//End
	}
}
</script>

<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S051_S051R_S056_S056R_S058_S058R_PAGE, request.getSession())
		// Start : R5 Added
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())
		// End : R5 Added
		){%>

<c:set var="sectioName" value="${fn:toUpperCase(fn:substring(section, 0, 1))}${fn:toLowerCase(fn:substring(section, 1,fn:length(section)))}"></c:set>
<h2 class='capitalize wordWrap'>
	<c:choose>
		<c:when test="${section eq 'servicessummary'}">
			<label class='floatLft'>Services: ${serviceName}</label>
		</c:when>
		<c:otherwise>
			<label class='floatLft'>${section} Documents</label>
		</c:otherwise>
	</c:choose>
	<c:if test="${section eq 'servicessummary'}">
		<c:choose>
			<c:when test="${loReadOnlySection}">
				<a id="returnSummaryPage" title="Service Summary"  class="floatRht returnButton"  href="<portlet:renderURL><portlet:param name='business_app_id' value='${business_app_id}'/><portlet:param name='elementId' value='${elementId}' /><portlet:param name='section' value='${section}'/><portlet:param name='service_app_id' value='${service_app_id}'/><portlet:param name='subsection' value='summary'/><portlet:param name='next_action' value='checkForService'/><portlet:param name='displayHistory' value='displayHistory'/></portlet:renderURL>">Service Summary</a>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${!loReadOnlySection and (loReadOnlyStatus ne null and (loReadOnlyStatus eq 'Returned for Revision' or loReadOnlyStatus eq 'Deferred'))}">
						<a id="returnSummaryPage" title="Service Summary"  class="floatRht returnButton"  href="<portlet:renderURL><portlet:param name='business_app_id' value='${business_app_id}'/><portlet:param name='elementId' value='${elementId}' /><portlet:param name='section' value='${section}'/><portlet:param name='service_app_id' value='${service_app_id}'/><portlet:param name='subsection' value='summary'/><portlet:param name='next_action' value='checkForService'/><portlet:param name='displayHistory' value='displayHistory'/></portlet:renderURL>">Service Summary</a>
					</c:when>
					<c:otherwise>
						<c:set var="urlApplication"
						value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_add_service&_nfls=false&bussAppStatus=${bussAppStatus}&business_app_id=${business_app_id}&applicationId=${applicationId}&section=servicessummary&subsection=summary&next_action=checkForService"></c:set>              
						<a id="returnSummaryPage" title="Service Summary"  class="floatRht returnButton"  href="${urlApplication}">Service Summary</a>
					</c:otherwise>							
				</c:choose>
			</c:otherwise>
		</c:choose>
	
	</c:if>
</h2>
<div class='hr'></div>
<div class="messagediv" id="messagediv"></div>

<h3 class="floatLft">Documents</h3>
<div class='floatRht'>
<c:if test="${section eq 'servicessummary' and org_type eq 'provider_org'}">
					<c:if test="${serviceComments ne null and ! empty serviceComments}">
							<%@include file="showServiceCommentsLink.jsp" %>
							 <div class="commentHidden" style="padding:10px;">
								<c:forEach var="loopItems" items="${serviceComments}" varStatus="counter">
								     	<c:if test="${counter.index ne 0}">
								     	 -------------------------------------------------<br>
								     	</c:if>
							     <b>${loopItems['USER_ID']} - <fmt:formatDate pattern="MM/dd/yyyy" value="${loopItems['AUDIT_DATE']}" /></b><br>
							      ${loopItems['DATA']}	<br>
						     </c:forEach>
					     </div>
					</c:if>
	</c:if>
</div>
<div class='clear'></div>
<c:choose>
	<c:when test="${section eq 'servicessummary'}">
	
	<div class="messagedivNyc" id="messagedivNyc"></div>
		<p>
			Based on the answers to the questions on the previous page, this page will list any documents required to complete your Service Application. Please upload any required documents or link to existing documents from your vault.
		</p>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${not empty taskItemList}">
				<p>
					Please upload most recent versions of the following required documents or select a document already uploaded from your Document Vault.
				</p>
			</c:when>
			<c:otherwise>
				<p>
				<!-- Fix for Defect 8284 -->
				You have not completed your organization’s ${sectioName} questions. Please complete the ${sectioName} questions in 
				order to view required ${sectioName} documents.
				<!-- Fix for Defect 8284 end -->
				</p>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>

<div class='tabularWrapper'>
<form name="myformupload" action="<portlet:actionURL/>" method ="post" >
<input type="hidden" value="" id="validateForm"/>
<input type="hidden" name="docTypeHidden" id="docTypeHidden" value='' />
<input type="hidden" name="docCatHidden" id="docCatHidden" value='' />
<!-- Modified for Release 3.1.0, Enhancement #6021; Adding new Hidden Type to contain DocType -->
<input type="hidden" value="" id="docType" name="docType"/>
 <input type="hidden" id="entityId" value="" name="entityId"/>
	<st:table objectName="taskItemList"  cssClass="heading"
		alternateCss1="evenRows" alternateCss2="oddRows" >
		<st:property headingName="Document Name" columnName="docName" align="center"
			size="20%">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameExtension" />
		</st:property>
		<st:property headingName="Document Type" columnName="docType"
			align="left" size="30%"  />
		<st:property headingName="Status" columnName="status"
			align="right" size="10%" />
		<st:property headingName="Modified" columnName="date"
			align="right" size="10%" />
		<st:property headingName="Last Modified By" columnName="lastModifiedBy"
			align="right" size="10%" />
		<st:property headingName="Actions" columnName="actions" 
			align="right" size="20%"  >
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentActionApplicationExtension" />
		</st:property>
	</st:table>
<div class="messagedivNycMsg" id="messagedivNycMsg" style='border:1px solid #ccc; border-top:0; padding:6px;' ><i>No documents required</i></div>
<c:if test="${!loReadOnlySection}">
	<div class="buttonholder">
		<input type="button" class="graybtutton" value="<< Back" title="<< Back" onclick="javascript: nextAction('back');"/>
		<input type="button" class="button" value="Next" title="Next" onclick="javascript: nextAction('save_next');" />
	</div>
</c:if>
</form>
<div id="overlayedJSPContent" style="display:none"></div>
</div>

<div class="overlay"></div>
<%-- Start : Changes in R5 --%>
<div class="alert-box alert-box-upload">
		<div class="content">
			<div id="newTabs"  class='wizardTabs wizardUploadTabs-align'>
				<div class="tabularCustomHead">Upload Document</div> 
				<h2 class='padLft'>Upload Document</h2>
				<div class='hr'></div>
				<ul>
					<li id='step1' class='active'>Step 1: File Selection</li>
					<li id='step2' style="padding-left:25px;">Step 2: Document Information</li>
					<li id='step3' class="last">Step 3: Document Location</li>
				</ul>
		       	<div id="tab1"></div>
		        <div id="tab2"></div>
		        <div id="tabnew"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" id="exitUpload" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
	</div>
	<div class="alert-box-link-to-vault">
	<div class="content">
  		<div id="newTabs">
			<div id="newTabs">
				<div class='tabularCustomHead'>Select Existing Document from Document Vault
					<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
				</div>
		        <div id="linktovault"></div>
			</div>
		</div>
  	</div>
</div>
<%-- End : Changes in R5 --%>
<div class="alert-box">
	<div class="content">
  		<div id="newTabs" class='wizardTabs'>
  			<div class='tabularCustomHead'>Upload Document
  				<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
  			</div>	
  			<h2 class='padLft'>Upload Document</h2>	
  			<div class='hr'></div>
			<ul>
				<li id="step1" class="active">Step 1: File Selection</li>
				<li id="step2" class="last">Step 2: Document Information</li>
			</ul>
            <div id="tab1"></div>
              <div id="tab2"></div>
		</div>
		<%-- Start : Changes in R5 --%>
		<a  href="javascript:void(0);" id="exitUpload" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
	</div>
	</div>
<div class="alert-box alert-box-viewDocumentProperties">
		<div class="content">
		
				<div class="tabularCustomHead">View Document Information</div>
				<div id="viewDocumentProperties"></div>
		
		</div>
		<a  href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
</div>
<%-- ENd : Changes in R5 --%>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>