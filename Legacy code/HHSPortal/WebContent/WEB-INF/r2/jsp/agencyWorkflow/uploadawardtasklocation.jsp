<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/uploadfile.js"></script>
<script>
var formAction;
var displayuploadForm;
function onReady(){
	formAction = document.displayuploadForm.action;
	displayuploadForm = document.displayuploadForm;
	if($js("#overlaytree").size()>0){
		$js("#overlaytree").jstree("destroy");
	    tree($('#hdnopenTreeAjaxUpload').val()+"&action=enhanceddocumentvault", 'overlaytree','customfolderid', "DocumentVault" ,'');
	}
	if("null" != '<%= request.getAttribute("message")%>'){
		$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
		$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
		$(".messagedivover").show();
	}
	
	$('#uploadDocument').click(function() {
		var url = $("#uploadDocumenturl").val()+"&submit_action=uploadFile&isAjaxCall=true";
		$(".alert-box").hide();
		pageGreyOut();
		hhsAjaxRender(null, document.displayuploadForm,'attachDocuments',url,"setErrorMessage");
		uploadGreyOut();
		$('#messagediv').html('The file was successfully uploaded to your Document Vault'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		$('#messagediv').removeClass('failedShow');
		$('#messagediv').addClass('passed');
		$('#messagediv').show();
	});
	
	$(".alert-box")
	.find('#back2')
	.unbind("click")
	.click(
			function() {
				pageGreyOut();
				displayuploadForm.action = $("#uploadbackStep3").val();
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
							$('ul')
									.removeClass(
											'ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
							$('#step1').removeClass().addClass(
									'default').css({
										"margin-left" : "25px"
										
									});;
							// Changing classes for Step - 2 in Release 5
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
				$(displayuploadForm).ajaxSubmit(options);
				return false;
			});
	
}
function onReadyLocation(){
	$js("#overlaytree").jstree("destroy");
	tree($('#hdnopenTreeAjaxUpload').val(), 'overlaytree','customfolderid', '' ,'');
	 
}
$(".alert-box").find('#cancel2').unbind("click").click(function() {
	$(".overlay").closeOverlay();
});
</script>
<div>
	<div id="treeStructure" style='width: 680px; padding-left: 15px;'>
	<div>Select the folder location to upload your document</div>
    <link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
    <link rel="stylesheet" href="../css/style.css" type="text/css"></link>
	<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
	<portlet:defineObjects/>
	<portlet:resourceURL var="openTreeAjax" id="openTreeAjax" escapeXml="false"></portlet:resourceURL>
	<portlet:actionURL var="uploadDocumentUrl" escapeXml="false">
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<portlet:param name="workflowId" value="${workflowId}" />
	</portlet:actionURL>
	<portlet:actionURL var='stepthreeback' id='stepthreeback' escapeXml='false'>
		<portlet:param name="submit_action" value="goBackActionFromStep3" />
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<portlet:param name="workflowId" value="${workflowId}" />
	</portlet:actionURL>
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<input type="hidden" name="hdnopenTreeAjaxUpload" id ="hdnopenTreeAjaxUpload" value='${openTreeAjax}' />
	<input type="hidden" name="uploadDocumenturl" id="uploadDocumenturl" value='${uploadDocumentUrl}' /> 
	<form action="#" method="post" id='displayuploadForm' name='displayuploadForm'>
	<input type="hidden" name="customfolderid" id="customfolderid" value='' />
	<input type = 'hidden' value='${stepthreeback}' id='uploadbackStep3'/>
	<div id='overlaytree' class='leftTreeOverlay formcontainer'></div>
	<div class='buttonholder buttonholder-align1'  id='btnholder'>
		<input type="button" value="Cancel" title="Cancel" name="cancelTaskDocument1" id="cancelTaskDocument1" class="graybtutton" /> 
		<input type="button" title="Back" name="back2" id="back2" value="Back" class="graybtutton" /> 
		<input type="button" value="Upload Document" title="Upload Document" name="uploadDocument" id="uploadDocument" /> 
		<input type="hidden" name="OldDocumentIdReq" id="OldDocumentIdReq" value='${OldDocumentIdReq}' />
	</div>
	</form>
	</div>
	
</div>
