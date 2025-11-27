/**
 * This file contains method to view document properties
 * overlay, upload screen overlays.
 */
var formAction;
//ready function
$(document).ready(function(){
	$("#fundingSpan").changeCurrency();
	});
/**
* this function implements the functionality of viewing document information
* corresponding to document id and document name
* R5 Added : documentType
* */
function viewDocumentInfo(documentId,documentType,documentStatus) {
	$("#hiddenDocumentId").val(documentId);
	$("#hiddenDocumentStatus").val(documentStatus);
	var jspPath = $("#jspPath").val();
	// Start || Modified as a part of Enhancement #5688 for Release 3.2.0
	if($("#isForCityTask").val() == "true")
	{ 
		$("#controller_action").val("agencyWorkflowCity");
	}
	// End || Modified as a part of Enhancement #5688 for Release 3.2.0
	$("#viewResponseForm").attr(
			"action",
			$("#viewResponseForm").attr("action")
					+ "&submit_action=viewDocumentInfo&lsViewDocInfoFromAgency=true&procurementId="
					+ $("#procurementId").val()+"&jspPath="+jspPath + "&fromAwardTask=" + $("#fromAwardTask").val()+"&docTypeHidden="+documentType); // R5 Added documentType
	var options = {
		success : function(responseText, statusText, xhr) {
			var $response = $(responseText);
			var data = $response.contents().find(".overlaycontent");
			$("#viewDocumentProperties").html(data.detach());
			$(".overlay").launchOverlay($(".alert-box-viewDocumentProperties"),
					$(".exit-panel.upload-exit"), "600px", null, "onReady");
			$('ul')
					.removeClass(
							'ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
			$('li')
					.removeClass(
							'ui-state-default ui-corner-top ui-state-hover');
			removePageGreyOut();
		},
		error : function(xhr, ajaxOptions, thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	pageGreyOut();
	$("#viewResponseForm").ajaxSubmit(options);
}

/**
 * This method is used to upload ABFO Document
*  Updated Method in R4
*  */
function uploadDocument1() {
	$("#uploadingDocumentType").val("BAFO");
	$("#docType").val("Best and Final Offer (BAFO)");
	
    uploadDocument($("#uploadProposalDocumentAction").val());
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
				}
				$("#overlayedJSPContent").html($response);
				// Start Updated in R5
				$(".overlay").launchOverlay($(".alert-box-upload"), $(".exit-panel.upload-exit"), "800px", null, "onReady");
				$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
				$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
				$('#step1').removeClass().addClass('active').css({'margin-left':'10px'});
				$('#step2').removeClass().css({'padding':'0 16px'});
				$('#step3').removeClass().addClass('last').css({'padding':'0 10px'});
				// End Updated in R5
				removePageGreyOut();
			},
			error:function (xhr, ajaxOptions, thrownError)
			{                     
				showErrorMessagePopup();
				removePageGreyOut();
			}
	};
	$(document.viewResponseForm).ajaxSubmit(options);
	return false;
}

/**
 * This will execute when upload button is clicked
 * */
function uploadDocument(formaction) {
	pageGreyOut();
	document.viewResponseForm.action = formaction
			+ '&submit_action=uploadProposalDocument';
}
