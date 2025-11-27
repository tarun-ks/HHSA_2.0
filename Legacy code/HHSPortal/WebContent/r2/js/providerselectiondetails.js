/**
 * ===========================================================
 * This file contains the methods that will handle the events 
 * when a provider will upload the document.
 * ===========================================================
 */

/**
 * this method will be executed when a user will select upload document option from the action drop down
 * This Method is modified to fix defect 5523 for release 2.6.0 to add new parameter "isRequired".
 **/
function actionDropDownChanged(documentId, selectElement, documentName,docStatus,docRefSeqNo,docType,isRequired)
{   
	// Start Added in R5
	$("#docTypeHidden").val(docType);
	// End Added in R5
	$("#docType").val(docType);
	$("#hiddendocRefSeqNo").val(docRefSeqNo);
	$("#uploadingDocumentType").val("Award");
	$("#hiddenDocumentId").val(documentId);
	if(document.getElementById("hiddenIsDocRequired")!=null){
	$("#hiddenIsDocRequired").val(isRequired);
	}
	var selectedOption = $(selectElement).find("option:selected").val();
	if (selectedOption == 'Upload Document') {
    uploadDocument($("#uploadAwardDocumentAction").val());
	var options = 
    	{	
			success: function(responseText, statusText, xhr ) 
			{
				var $response=$(responseText);
			    var data = $response.contents().find(".overlaycontent");
			   	$("#tab1").empty();
				$("#tab2").empty();
				if(data != null || data != ''){
			    	$("#tab1").html(data.detach());
				}
				$("#overlayedJSPContent").html($response);
				// Start Updated in R5
				$(".overlay").launchOverlayNoClose($(".alert-box-upload"), "850px", null, "onReady");
				// End Updated in R5
				//binds close event 
				$(".exit-panel.upload-exit").unbind('click').click(function(e){
					if(overlayLaunched != null){
						e.stopPropagation();
						overlayLaunched.closeOverlay(e);
						$("#tab1").html("");
					}
				});
				selectElement.selectedIndex = "";
				$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
				$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
				//Added for Release 5
				$('#step1').removeClass().addClass('active').css('margin-left','0px');
				$('#step2').removeClass().css({'margin-left':'0px','padding-left':'30px'});
				$('#step3').removeClass().addClass('last').css('margin-left','0px');
				//R5 end
				removePageGreyOut();
			},
			error:function (xhr, ajaxOptions, thrownError)
			{                     
				showErrorMessagePopup();
				removePageGreyOut();
			}
	};
/**
 *  AJAX submission for add award document
*	Updated Method in R4
**/
	$(document.awardDocform).ajaxSubmit(options);
	selectElement.selectedIndex = "";
	return false;
	}if (selectedOption=='Select Document from Vault')
		{
		pageGreyOut();
		// Start || Changes made for defect #6446 for Release 3.2.0
		var docTypeActual = docType;
		var docType = encodeURIComponent(docTypeActual);
		// End || Changes made for defect #6446 for Release 3.2.0
		selectElement.selectedIndex = "";
		// Start Updated in R5
	    var url = $("#addAwardDocumentResource").val()+"&docType="+docType+"&hiddendocRefSeqNo="+docRefSeqNo+"&hiddenIsDocRequired="+isRequired+"&awardId="+$("#awardId").val()+"&uploadingDocumentType=Award&hiddenDocumentId="+documentId+"&selectVault=true&selectAll=true";
	    // End Updated in R5
	    var jqxhr = $
		.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
				if(response != null || response != ''){
			    	$("#addDocumentFromVault").html(response);
				}
				// Start Updated in R5
				$(".overlay").launchOverlay($(".alert-box-addDocumentFromVault"), $(".exit-panel.upload-exit"), "850px", "650px", "onReady");
				// End Updated in R5
				removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
	if (selectedOption == 'View Document') {
		viewRFPDocument(documentId, documentName);
		selectElement.selectedIndex = "";
	} else if (selectedOption == 'View Document Information') {
		$("#hiddenDocumentId").val(documentId);
		$("#uploadingDocumentType").val("Award");
		$("#hiddenDocumentStatus").val(docStatus);
		$("#hiddendocRefSeqNo").val(docRefSeqNo);
		$("#awardDocform").attr("action", $("#viewDocumentInfoResource").val());
		var options = 
	    			{	
					   	success: function(responseText, statusText, xhr ) 
						{
							var $response=$(responseText);
	                        var data = $response.contents().find(".overlaycontent");
	                        $("#viewDocumentProperties").html(data.detach());
	                        $("#overlayedJSPContent").html($response);
							$(".overlay").launchOverlay($(".alert-box-viewDocumentProperties"), $(".exit-panel.upload-exit"), "800px", null, "onReady");
							var a=$('.documentLocationPath').text().trim();
							a=a.replace(/\\/g, "&#x200b;\\&#x200b;");
							b='<div style="width:50ch;" ></div>';
							$('.documentLocationPath').html(b);
							$('.documentLocationPath div').html(a);
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
					pageGreyOut();
					$("#awardDocform").ajaxSubmit(options);
					selectElement.selectedIndex = "";
					return false;
	} else if (selectedOption == 'Remove Document') {
		pageGreyOut();
		selectElement.selectedIndex = "";
		$("#deleteDocumentId").val(documentId);
		$("#docType").val(docType);
		$("#hiddendocRefSeqNo").val(docRefSeqNo);
		$(".overlay").launchOverlay($(".alert-box-delete"), $(".exit-panel"), "350px", null, "onReady");
		removePageGreyOut();
		return false;
	}
}
/**
 * This will execute when upload button is clicked
 * */
function uploadDocument(formaction) {
	pageGreyOut();
	document.awardDocform.action = formaction
			+ '&submit_action=uploadProposalDocument';
}

/**
 * This will be executed when any option is selected from the action drop down
 * */
function actionDropDownChangedComplete(documentId, selectElement, documentName,docStatus,docRefSeqNo,docType) {
	var value = selectElement.selectedIndex;
	
}


/**
 *  On page Load
*   Updated Method in R4
*   */
$(document)
.ready(
		function() {
			$("#proposalSummaryLink").click(function() {
				$("#awardDocform").attr("action",
						$("#proposalSummaryUrl").val());
				pageGreyOut();
				document.awardDocform.submit();
			});
			$("#returnSelectionDetailsSummary").click(
					function() {
						$("#navigationForm").find("#forAction").eq(0).val(
								"selectionDetail");
						$("#navigationForm").find("#topLevelFromRequest").eq(0)
								.val("SelectionDetails");
						$("#navigationForm").find("#render_action").eq(0)
						.val("viewSelectionDetails");
						pageGreyOut();
						document.navigationForm.submit();
					});
});

/**
 * Below method is used to refresh the document list screen after updating the document properties.
 * */
function navigateToMain(){
	pageGreyOut();
    var url = $("#viewSelectionDetailsURL").val();
    location.href=url;
}