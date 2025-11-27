/**
 * ==============================================================
 * This js file is used to handle all the client side activities
 * performed by the user on rfp document screen.
 * ==============================================================
 */
var responsetotal = "";
// On page load
$(document).ready(function() {
					//Below code will be executed when the user click on add document button on the Rfp document screen
					$('#addDocument').click(function() {
						pageGreyOut();
					    var url = $("#addRfpDocumentResource").val();
					    var jqxhr = $
						.ajax({
							url : url,
							type : 'POST',
							cache : false,
							success : function(response) {
								if(response != null || response != ''){
							    	$("#addDocumentFromVault").html(response);
								}
								$(".overlay").launchOverlay($(".alert-box-addDocumentFromVault"), $(".exit-panel.upload-exit"), "850px", null, "onReady");
								removePageGreyOut();
							},
							error : function(data, textStatus, errorThrown) {
								showErrorMessagePopup();
								removePageGreyOut();
							}
						});
					});
					//on click of next button
					$('#nextButton').click(function() {
						pageGreyOut();
						$("#proposalDocsForm").attr("action",
								$("#proposalSubmitUrl").val());
									document.proposalDocsForm.submit();
					});
					//on click of back button
					$('#backButton').click(function() {
						pageGreyOut();
						$("#proposalDocsForm").attr("action",
														$("#editProposal").val());
						document.proposalDocsForm.submit();			
					});
					//on click of return to proposal Summary hyperlink
					$("#returnProposalSummaryPageDocuments").click(function(){
						document.proposalDocsForm.action = $("#proposalSummaryUrl").val();
						document.proposalDocsForm.submit();
					});
					//shows proposal comments
					$("#showProposalComment").click(function(){
						fillAndShowOverlay();
					});
				});

/**
 * Below method is used to refresh the document list screen after updating the document properties.
 * */
function navigateToMain(){
	pageGreyOut();
    var url = $("#rfpDocumentRender").val();
    location.href=url;
}
/**
 * this function closes the show comments overlay
 * */
function clearAndCloseOverLay() {
	$("#overlayDivId").html("");
	$(".overlay").closeOverlay();
}

/**
 * this function launches the show comments overlay
 * */
function fillAndShowOverlay() {
	pageGreyOut();
	var v_parameter = "";
	var urlAppender = $("#showProposalCommentsResourceUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#overlayDivId").html(e);
			$(".overlay").launchOverlayNoClose($(".alert-box-proposal-comments"), null, null);
			$("a.exit-panel").click(function() {
				clearAndCloseOverLay();
			});
			removePageGreyOut();
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
}
/**
 * This will execute when any Column header on grid is clicked for sorting
*/
function sort(columnName) {
	var sortType = $("#sortType").val();
	if (sortType == "asc") {
		sortType = "desc";
	} else {
		sortType = "asc";
	}
	$("#proposalDocsForm")
			.attr(
					"action",
					$("#proposalDocsForm").attr("action")
							+ "&next_action=sortRfpDocument&sortGridName=procurementRoadMap&columnName="
							+ columnName + "&sortType=" + sortType);
	document.proposalDocsForm.submit();
}



/**
 * This will be executed when any option is selected from the action drop down
 * */
function actionDropDownChanged(documentId, selectElement, documentName,docStatus,procurementDocId,docType) {
	var selectedOption = $(selectElement).find("option:selected").val();
	// Start Added in R5
	$("#docTypeHidden").val(docType);
	// End Added in R5
	$("#hiddenDocumentId").val(documentId);
	$("#hiddenDocumentStatus").val(docStatus);
	$("#hiddendocRefSeqNo").val(procurementDocId);
	$("#proposalDocsForm").attr("action", $("#viewDocumentInfoResource").val());
	$("#docType").val(docType);
	if (selectedOption == 'View Document') {
		viewRFPDocument(documentId, documentName);
		selectElement.selectedIndex = "";
	} else if (selectedOption == 'View Document Information') {
		var options = 
	    			{	
					   	success: function(responseText, statusText, xhr ) 
						{
							var $response=$(responseText);
							var data = $response.contents().find(".overlaycontent");
	                        $("#viewDocumentProperties").html(data.detach());
	                        $("#overlayedJSPContent").html($response);
							$(".overlay").launchOverlay($(".alert-box-viewDocumentProperties"), $(".exit-panel.upload-exit"), "550px", null, "onReady");
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
					$(proposalDocsForm).ajaxSubmit(options);
					selectElement.selectedIndex = "";
					return false;
	} else if (selectedOption == 'Remove Document') {
		pageGreyOut();
		selectElement.selectedIndex = "";
		$("#deleteDocumentId").val(documentId);
		$("#docType").val(docType);
		$("#hiddendocRefSeqNo").val(procurementDocId);
		$(".overlay").launchOverlay($(".alert-box-delete"), $(".exit-panel"), "350px", null, "onReady");
		//$.unblockUI();
		removePageGreyOut();
		return false;
	}
	else if (selectedOption == 'Upload Document') {
		$("#uploadingDocumentType").val("Proposal");
    uploadDocument($("#uploadProposalDocumentAction").val());
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
				$(".overlay").launchOverlay($(".alert-box-upload"), $(".exit-panel.upload-exit"), "850px", null, "onReady");
				selectElement.selectedIndex = "";
				$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
				$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
				$('#step1').removeClass().addClass('active').css('margin-left','0px');
				$('#step2').removeClass().css({'margin-left':'0px','padding-left':'30px'});
				$('#step3').removeClass().addClass('last').css('margin-left','0px');
				// End Updated in R5
				removePageGreyOut();
			},
			error:function (xhr, ajaxOptions, thrownError)
			{                     
				showErrorMessagePopup();
				removePageGreyOut();
			}
	};
	$(document.proposalDocsForm).ajaxSubmit(options);
	selectElement.selectedIndex = "";
	return false;
	}else if (selectedOption=='Select Document from Vault')
		{
		pageGreyOut();
		// Start || Changes made for defect #6446 for Release 3.2.0
		var docTypeActual = docType;
		var docType = encodeURIComponent(docTypeActual);
		// End || Changes made for defect #6446 for Release 3.2.0
		//added selectVault flag for Release 5
	    var url = $("#addProposalDocumentResource").val()+"&docType="+docType+"&hiddendocRefSeqNo="+procurementDocId+"&uploadingDocumentType=Proposal&hiddenDocumentId="+documentId+"&selectVault=true&selectAll=true";
	    var jqxhr = $
		.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
				if(response != null || response != ''){
			    	$("#addDocumentFromVault").html(response);
				}
				$(".overlay").launchOverlay($(".alert-box-addDocumentFromVault"), $(".exit-panel.upload-exit"), "850px", "636px", "onReady");
				selectElement.selectedIndex = "";
				removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
}
}
/**
 * This will execute when upload button is clicked
 * */
function uploadDocument(formaction) {
	pageGreyOut();
	document.proposalDocsForm.action = formaction
			+ '&submit_action=uploadProposalDocument';
}