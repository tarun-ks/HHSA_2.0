/**
 * ===============================================================
 * This js file is used to handle all the client side activities 
 * performed by the user on rfp document screen.
 * ===============================================================
 */
var responsetotal = "";
$(document).ready(function() {
				// This will execute when upload button is clicked
					$('#uploadDoc').click(function() {
						pageGreyOut();
						$("#rfpReleaseForm").attr("action", $("#uploadRfpDocumentAction").val());
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
						$('#step1').removeClass().addClass('active').css('margin-left','0px');
						$('#step2').removeClass().css('margin-left','0px');
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
						$(document.rfpReleaseForm).ajaxSubmit(options);
						return false;
					});
					//Below code will be executed when the user click on add document button on the Rfp document screen
					$('#addDocument').click(function() {
						pageGreyOut();
						// Start Updated in R5
					    var url = $("#addRfpDocumentResource").val()+"&selectVault=true&selectAll=true";
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
								$(".overlay").launchOverlay($(".alert-box-addDocumentFromVault"), $(".exit-panel.upload-exit"), "850px", "auto", "onReady");
								// End Updated in R5
								removePageGreyOut();
							},
							error : function(data, textStatus, errorThrown) {
								showErrorMessagePopup();
								removePageGreyOut();
							}
						});
					});
				});

/**
 *  Below method is used to refresh the document list screen after updating the document properties.
 *  */
function navigateToMain(){
	pageGreyOut();
    var url = $("#rfpDocumentRender").val();
    location.href=url;
}
/**
 * This will execute when any Column header on grid is clicked for sorting
 * */
function sort(columnName) {
	var sortType = $("#sortType").val();
	if (sortType == "asc") {
		sortType = "desc";
	} else {
		sortType = "asc";
	}
	$("#rfpReleaseForm")
			.attr(
					"action",
					$("#rfpReleaseForm").attr("action")
							+ "&next_action=sortRfpDocument&sortGridName=procurementRoadMap&columnName="
							+ columnName + "&sortType=" + sortType);
	document.rfpReleaseForm.submit();
}


/**
*   This will be executed when any option is selected from the action drop down
*	Updated Method in R4
*/
function actionDropDownChanged(documentId, selectElement, documentName,isAddendumDoc,docStatus,docReferenceId,docType,isReadOnly) {
	var value = selectElement.selectedIndex;
	var selectedOption = $(selectElement).find("option:selected").val();
	if (value == 1) {
		viewRFPDocument(documentId, documentName);
		selectElement.selectedIndex = "";
	} else if (value == 2) {
		// Start Added in R5
		$("#docTypeHidden").val(docType);
		// End Added in R5
		$("#hiddenDocumentId").val(documentId);
		$("#hiddenDocumentStatus").val(docStatus);
		$("#hiddenAddendumType").val(isAddendumDoc);
		$("#pageReadOnly").val(isReadOnly);
		$("#rfpReleaseForm").attr("action", $("#viewDocumentInfoResource").val());
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
					$("#rfpReleaseForm").ajaxSubmit(options);
					selectElement.selectedIndex = "";
					return false;
	} else if (selectedOption == 'Remove Document from List') {
		pageGreyOut();
		selectElement.selectedIndex = "";
		$("#deleteDocumentId").val(documentId);
		$("#hiddenDocReference").val(docReferenceId);
		$(".overlay").launchOverlay($(".alert-box-delete"), $(".exit-panel"), "350px", null, "onReady");
		removePageGreyOut();
		return false;
	}
	else if (selectedOption == 'Replace Document By Uploading New Document') {
		pageGreyOut();
		$("#hiddenDocReference").val(docReferenceId);
		$("#hiddenDocumentId").val(documentId);
		$("#uploadProcess").val("ReplacingRfpDoc");
		$("#hiddenAddendumType").val(isAddendumDoc);
		$("#docType").val(docType);
		$("#rfpReleaseForm").attr("action", $("#uploadRfpDocumentAction").val());
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
		//Added for fix if defect # 8378
		$(".overlay").launchOverlay($(".alert-box-upload"), $(".exit-panel.upload-exit"), "800px", null, "onReady");
		$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
		$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
		$('#step1').removeClass().addClass('active').css('margin-left','0px');
		$('#step2').removeClass().css('margin-left','0px');
		$('#step3').removeClass().addClass('last').css('margin-left','0px');
		//Added for fix if defect # 8378 end
		removePageGreyOut();
		},
		error:function (xhr, ajaxOptions, thrownError)
		{                     
			showErrorMessagePopup();
			removePageGreyOut();
		}
		};
		selectElement.selectedIndex = "";
		$(document.rfpReleaseForm).ajaxSubmit(options);
		return false;
	
	}else if (selectedOption == 'Replace Document From Vault')
		{
		pageGreyOut();
		//Added for fix if defect # 8378
	    var url = $("#addRfpDocumentResource").val()+"&docType="+docType+"&uploadingDocumentTypeAdd=RFP&hiddenAddendumType="+isAddendumDoc
	    +"&replacingDocumentId="+documentId+"&hiddendocRefSeqNo="+docReferenceId+"&selectVault=true&selectAll=true";
	   	//Added for fix if defect # 8378 end 
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
 * This method will handle the action on click of Back button
 * */
function backButton() {
	window.location.href = $("#backPageURL").val();
}

/**
 * This method will handle the action on click of Next button
 * */
function nextButton() {
	window.location.href = $("#nextPageURL").val();
}