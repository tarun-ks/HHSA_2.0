<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/uploadfile.js"></script>
<script>
var formAction;
var displayuploadForm;
function onReady(){
	formAction = document.displayuploadForm.action;
	displayuploadForm = document.displayuploadForm;
	if("null" != '<%= request.getAttribute("message")%>'){
		$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
		$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
		$(".messagedivover").show();
		
	}
	var docType = $("#uploadingDocumentType").val();
	if(docType=="BAFO")
	{
		$("#overlaytree").hide();
		$("#uploadStep3NonBAFO").hide();
		$("#uploadStep3BAFO").show();
	 removePageGreyOut();
	}
	else
	{
	 $("#uploadStep3BAFO").hide();
	 $("#overlaytree").show();
		 onReadyLocation();
	} 
	
	var uploadfileOption = 
    {
    	success: function(responseText, statusText, xhr ) 
		{
    		var responseString = new String(responseText);
			var responsesArr = responseString.split("|");
			if(responsesArr[1] == "Error")
			{
				$( "#formcontainer1" ).show();
			}
			else if(responsesArr[1] == "Exception")
			{
				$(".alert-box-upload").show();
				$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
				$(".messagedivover").addClass(responsesArr[4]);
				$(".messagedivover").show();
				removePageGreyOut();
			}
			else if($("#uploadingDocumentType").val()=='RFP')
			{
				var url = $("#rfpDocumentRender").val()+"&success=upload"; 
				window.location.href=url;
			}else if($("#uploadingDocumentType").val()=='Proposal')
			{
				var url = $("#proposalDocumentRender").val()+"&success=upload"; 
				window.location.href=url;
			}else if($("#uploadingDocumentType").val()=='Award')
			{
				var url = $("#awardDocumentRender").val()+"&success=upload"; 
				window.location.href=url;
			}// Code updated for R4 Starts 
			else if($("#uploadingDocumentType").val()=='BAFO')
			{
				var url = $("#hiddenViewResponse").val()+"&proposalId="+$("#proposalId").val()+"&success=upload&removeMenu=sadasdasd&showBafoButton=true"; 
				window.location.href=url;
			}// Code updated for R4 Ends
			// Start || Changes done for Enhancement #6429 for Release 3.4.0
			else if($("#uploadingDocumentType").val()=='awardDoc')
			{
				var url = $("#awardDocumentRender").val()+"&proposalId="+$("#proposalId").val()
				+"&organizationId="+$("#organizationId").val()+"&uploadingDocumentType="+$("#uploadingDocumentType").val()+"&asProcStatus="+$("#asProcStatus").val()+"&isFinancials="+$("#isFinancials").val()+"&contractId="+$("#contractId").val()+"&success=upload&removeMenu=true"; 
				window.location.href=url;
			}
			removePageGreyOut();
		},
		error:function (xhr, ajaxOptions, thrownError)
		{   
			showErrorMessagePopup();
			removePageGreyOut();
		}
    };
	$('#uploadDocument').click(function() {
	document.displayuploadForm.action=$("#uploadDocumenturl").val()+"&isAjaxCall=true";
 	$("#displayuploadForm").ajaxSubmit(uploadfileOption);
	$(".alert-box").hide();
	uploadGreyOut();
	});
	
$(".alert-box-upload").find('#cancel2').unbind("click").click(function() {
pageGreyOut();
var docType = $("#uploadingDocumentType").val();
if (docType == 'BAFO'){
	closeTaskOverlay();
}
if (docType == 'awardDoc'){
	closeTaskOverlay();
}
else if(docType != 'BAFO'){
	pageGreyOut();
$("#displayuploadForm").attr(
		"action",
		$("#cancelUploadDocument").val());
document.displayuploadForm.submit();
}});
	
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
	if($js("#overlaytree").size()>0){
		$js("#overlaytree").jstree("destroy");
	    tree($('#hdnopenTreeAjaxUpload').val()+"&action=enhanceddocumentvault", 'overlaytree','customfolderid', "DocumentVault" ,'');
	}
	//$js("#overlaytree").jstree("destroy");
	//tree($('#hdnopenTreeAjaxUpload').val(), 'overlaytree','customfolderid', '' ,'');
	 
}
</script>
<div>
	<div id="treeStructure" style='width: 680px; padding-left: 15px;'>
	<div id="uploadStep3NonBAFO">Select the folder location to upload your document</div>
    <link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
    <link rel="stylesheet" href="../css/style.css" type="text/css"></link>
	<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
	<portlet:defineObjects/>
	<portlet:resourceURL var="openTreeAjax" id="openTreeAjax" escapeXml="false"></portlet:resourceURL>
	<portlet:actionURL var="uploadDocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="submit_action" value="uploadFile" />
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" />
		<portlet:param name="currentProcurementId" value="${procurementId}" />
		<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
		<portlet:param name="organizationId" value="${organizationId}" />
		<portlet:param name="userName" value="${userName}" />
		<portlet:param name="staffId" value="${staffId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
		<portlet:param name="isFinancials" value="${isFinancials}" />
		<portlet:param name="contractId" value="${hdncontractId}" />
		<!-- Fix for defect 8378 -->
		<portlet:param name="hiddenAddendumType" value="${isAddendumType}" />
		<portlet:param name="replacingDocumentId" value="${replacingDocumentId}" />
		<!-- Fix for defect 8378 end-->
	</portlet:actionURL>
	<portlet:renderURL var="rfpDocumentRenderUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="render_action" value="displayRFPDocumentList" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:renderURL>
	<portlet:renderURL var="proposalDocumentRenderUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="render_action" value="procurementProposalDocumentList" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:renderURL>
	<portlet:renderURL var="awardDocumentRenderUrl" escapeXml="false">
		<portlet:param name="action" value="selectionDetail" />
		<portlet:param name="render_action" value="viewSelectionDetails" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="awardId" value="${awardId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:renderURL>
	<portlet:renderURL var='viewResponse' id='viewResponse' escapeXml='false'>
		<portlet:param name="action" value="propEval"/>
		<portlet:param name="render_action" value="viewResponse"/>
		<portlet:param name="procurementId" value='${procurementId}'/>
		<portlet:param name="proposalId" value='${proposalId}'/>
		<portlet:param name="jspPath" value="evaluation/"/>
		<portlet:param name="IsProcDocsVisible" value="true"/>
	</portlet:renderURL>
	<portlet:actionURL var='stepthreeback' id='stepthreeback' escapeXml='false'>
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="submit_action" value="goBackActionFromStep3" />
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
		<portlet:param name="hiddendocRefSeqNo" value="${hiddendocRefSeqNo}" />
		<portlet:param name="currentProcurementId" value="${procurementId}" />
		<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
		<portlet:param name="organizationId" value="${organizationId}" />
		<portlet:param name="userName" value="${userName}" />
		<portlet:param name="staffId" value="${staffId}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
		<portlet:param name="isFinancials" value="${isFinancials}" />
		<portlet:param name="contractId" value="${hdncontractId}" />
	</portlet:actionURL>
	<portlet:actionURL var="cancelUploadDocumentUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="submit_action" value="cancelUploadDocument" />	
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
		<portlet:param name="uploadingDocumentType" value="${uploadingDocumentType}" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
		<%-- Code updated for Enhancement #6429 for Release 3.4.0 Starts--%>
		<portlet:param name="organizationId" value="${organizationId}" />
		<portlet:param name="isFinancials" value="${isFinancials}" />
		<%-- Code updated for Enhancement #6429 for Release 3.4.0 Ends--%>
</portlet:actionURL>
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<input type="hidden" name="hdnopenTreeAjaxUpload" id ="hdnopenTreeAjaxUpload" value='${openTreeAjax}' />
	<input type="hidden" name="uploadDocumenturl" id="uploadDocumenturl" value='${uploadDocumentUrl}' /> 
	<input type="hidden" value="${cancelUploadDocumentUrl}" id="cancelUploadDocument"/>
	<form action="#" method="post" id='displayuploadForm' name='displayuploadForm'>
	<input type="hidden" value="<%=document.getFilePath()%>" name="filepath" /> 
	<input type="hidden" value="<%=document.getDocCategory()%>" name="documentCategory" /> 
	<input type="hidden" value="<%=document.getDocType()%>" name="documentType" id='documentType' />
	<input type="hidden" value="uploadrfpdocumentlocation" name="jspName" /> 
	<input type="hidden" id="message" name="message" /> 
	<input type="hidden" id="messageType" name="messageType" />
	<input type="hidden" id="next_action" name="next_action" value='' />
	<input type="hidden" name="customfolderid" id="customfolderid" value='' />
	<input type="hidden" value="${rfpDocumentRenderUrl}" id="rfpDocumentRender"/>
	<input type="hidden" value="${proposalDocumentRenderUrl}" id="proposalDocumentRender"/>
	<input type="hidden" value="${awardDocumentRenderUrl}" id="awardDocumentRender"/>
	<input type="hidden" value="${uploadingDocumentType}" id="uploadingDocumentType"/>
	<input type="hidden" value="${isFinancials}" id="isFinancials"/>
	<input type = 'hidden' value='${hdncontractId}' id='contractId' name='contractId' />
	<input type="hidden" id="asProcStatus" value="${asProcStatus}" name="asProcStatus"/>
	<input type="hidden" value="${filelocationUrl}" id="filelocation"/>
	<input type = 'hidden' value='${viewResponse}' id='hiddenViewResponse'/>
	<input type = 'hidden' value='${stepthreeback}' id='uploadbackStep3'/>
	<input type="hidden" value="${proposalId}" id="proposalId" name="proposalId">
	<input type="hidden" value="${awardId}" id="awardId" name="awardId">
	<input type="hidden" value="${evaluationPoolMappingId}" id="evaluationPoolMappingId" name="evaluationPoolMappingId">
	<input type="hidden" id="hiddenDocReference" value="${hiddendocRefSeqNo}" name="hiddenDocReference"/>
	<input type="hidden" id="replacingDocumentId" value="${replacingDocumentId}" name="replacingDocumentId"/>
	<input type="hidden" id="message" name ="message"/>
	<input type="hidden" id="messageType" name ="messageType"/>
	<input type="hidden" id="next_action" name ="next_action"/>
	<input type="hidden" value="${uploadProcess}" id="uploadProcess" name="uploadProcess"/>
	<input type="hidden" id="hiddenAddendumType" value="${isAddendum}" name="hiddenAddendumType"/>
	<input type="hidden" value="${organizationId}" id="organizationId"/>
	<input type="hidden" name="isAjaxCall" value="true" />
	<div id='overlaytree' class='leftTreeOverlay formcontainer'></div>
		<div id="uploadStep3BAFO" style="display: none">
				<div style="height: 175px;" class="pad10">
					This document will be uploaded to the Provider's Document Vault root folder.<br/><br/>
					Click the "Upload Document" button to continue.<br/>
				</div>
		</div>
	<div class='buttonholder buttonholder-align1'  id='btnholder'>
		<input type="button" value="Cancel" title="Cancel" name="cancel2" id="cancel2" class="graybtutton" /> 
		<input type="button" title="Back" name="back2" id="back2" value="Back" class="graybtutton" /> 
		<input type="button" value="Upload Document" title="Upload Document" name="uploadDocument" id="uploadDocument" /> 
		<input type="hidden" name="OldDocumentIdReq" id="OldDocumentIdReq" value='${OldDocumentIdReq}' />
	</div>
	</form>
	</div>
	
</div>
