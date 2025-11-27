/**
 *  ready function implements the functionality to convert award amount in currency format and to make
 *	the header read-only
 *	Updated Method in R4
**/
$(document).ready(function(){
	$(".wlp-bighorn-header").hide();
	$(".footer").hide();	
	$('#downloadAllDocs').click(function() {
		pageGreyOut();
		var orgname = $("#organisation_name").text();
		orgname = orgname.replace(/[^a-zA-Z0-9 ]/g,'');
		var urlAppender = $("#downloadDocumentUrl").val() + "&organizaionName="+orgname;
		$.ajax({
			type : "POST",
			cache : false,
			url : urlAppender,
			success : function(data) {
				data = decodeHTMLEntities(data);
				var dataJSON = $.parseJSON(data);
				if (dataJSON != null && dataJSON.output != null) {
					if (dataJSON.output[0].error != null) {
						$("#jsMessageContent").html(dataJSON.output[0].error);
						$("#jsmessagediv").show();
					} else {
						var filePath = dataJSON.output[0].path;
						window.location.href = ($("#contextPathSession").val()
								+ "/dbdDoc/" + filePath);
					}
				}
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	});
	// This function is used to remove document based on
	// document Id.Added for Enhancement #6429 for Release 3.4.0
	$('#deleteDoc').click(
			function() {
				$(".overlay").closeOverlay();
				pageGreyOut();
				$("#uploadingDocumentType").val("awardDoc");
				$("#awardDocform")
				.attr("action",
						$("#deleteDocument").val()+"&removeMenu=true");
				$("#awardDocform").submit();
			});
});
/**
 * This function is used to decode HTML Entities.
 * */
function decodeHTMLEntities(text) {
    var entities = [
        ['apos', '\''],
        ['amp', '&'],
        ['lt', '<'],
        ['gt', '>']
    ];
    for (var i = 0, max = entities.length; i < max; ++i) 
        text = text.replace(new RegExp('&'+entities[i][0]+';', 'g'), entities[i][1]);
    return text;
}

/**
 * This will be executed when any option is selected from the action drop down
Changes done for Enhancement #6429 for Release 3.4.0
*/
function actionDropDownChangedComplete(documentId, selectElement, documentName,docStatus,docRefSeqNo,docType,agencyAward) {
	var value = selectElement.selectedIndex;
	if (value == 1) {
		viewRFPDocument(documentId, documentName);
		selectElement.selectedIndex = "";
	} else if (value == 2) {
		pageGreyOut();
		/* Start Added in R5*/
		$("#docTypeHidden").val(docType);
		/* End Added in R5*/
		$("#hiddenDocumentId").val(documentId);
		$("#uploadingDocumentType").val("Award");
		$("#hiddenDocumentStatus").val(docStatus);
		$("#hiddendocRefSeqNo").val(docRefSeqNo);
	/*	 Start ||Changes done for Enhancement #6429 for Release 3.4.0*/
		if(agencyAward == "1"){
			$("#awardDocform").attr("action",$("#viewDocumentInfoResource").val());
		}
		else{
			$("#awardDocform").attr("action",$("#viewDocumentInfoResource").val()
					+ "&lsViewDocInfoFromAgency=true");	
		}
		/* End || Changes done for Enhancement #6429 for Release 3.4.0*/
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
					
					$("#awardDocform").ajaxSubmit(options);
					selectElement.selectedIndex = "";
					return false;
	} 
	/* Start || Changes done for Enhancement #6429 for Release 3.4.0*/
	else if (value == 3) {
		pageGreyOut();
		selectElement.selectedIndex = "";
		$("#deleteDocumentId").val(documentId);
		$("#docType").val(docType);
		$("#hiddendocRefSeqNo").val(docRefSeqNo);
		$(".overlay").launchOverlay($(".alert-box-delete"), $(".exit-panel"), "350px", null, "onReady");
		removePageGreyOut();
		return false;
	}
	/* End || Changes done for Enhancement #6429 for Release 3.4.0*/
}

/**
 * This function is used to view contract COF
 * */
function viewFinancialContractDocument(element)
{
	element.selectedIndex = "";
	$('#CofHref').click();
}

/**
 * This function is used to view budget summary screen
 * */
function viewBudgetTypeDocument(element)
{
	element.selectedIndex = "";
	$('#providerHref').click();
}

/**This function is used to view budget summary screen*/
function viewBudgetTypeDocument1()
{
	$('#providerHref').click();
}

/**
 * This function is used to view contract COF
 **/
function viewFinancialContractDocument1()
{
	$('#CofHref').click();
}

/** 
 * Start of Changes done for Build 3.1.0 Enhancement 6025
This function is used for zip dropdown*/
function zipDocument(element){
	var value = $(element).val();
	if(value == "Request New Zip File"){
		$.blockUI({
        	message: "<img src='../framework/skins/hhsa/images/loadingBlue.gif' />",
        	overlayCSS: { opacity : 0.8}
        });
	
		var urlAppender = $("#hiddenRequestZipUrl").val() + "&docStatus="
		+ $("#docStatus").val();
		hhsAjaxRender(null, document.awardDocform, 'reRenderDocId', urlAppender);
	}
	
	else if(value == "Download Zip File"){
		downloadAmendmentDocumentUrl();
	}
	$('#actions').val('"I need to...');
}

/**
 * This function is used to provide Url for download Amendment Documents
 * */
function downloadAmendmentDocumentUrl(){
	pageGreyOut();
	window.open($("#contextPathSession").val() + "/GetContent.jsp?downloadZip=downloadZip&fileName=" + $("#fileName").val() + "&providerOrgID="
	+ $("#providerOrgID").val() + "&docStatus=" + $("#docStatus").val() + "&procurementId=" + $("#procurementId").val() + 
	"&evaluationPoolMappingId=" + $("#evaluationPoolMappingId").val()) ; 
	removePageGreyOut();
}

/**
 * End of Changes done for Build 3.1.0 Enhancement 6025
This method is used to upload agency award Document
Added for Enhancement #6429 for Release 3.4.0
*/
function addAwardDocument() {
	/* Start Added in R5*/
	pageGreyOut();
/*	 End Added in R5*/
	$("#uploadingDocumentType").val("awardDoc");
    uploadDocument($("#addAwardDocumentAction").val());
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
				/* Start Added in R5*/
				$(".overlay").launchOverlay($(".alert-box-upload"), $(".exit-panel.upload-exit"), "800px", null, "onReady");
				$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
				$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
				$('#step1').removeClass().addClass('active').css('margin-left','0px');
				$('#step2').removeClass().css('margin-left','0px');
				$('#step3').removeClass().addClass('last').css('margin-left','0px');
				/* End Added in R5*/
				removePageGreyOut();
			},
			error:function (xhr, ajaxOptions, thrownError)
			{                     
				showErrorMessagePopup();
				removePageGreyOut();
			}
	};
	$(document.awardDocform).ajaxSubmit(options);
	return false;
}

/**
 * This will execute when upload button is clicked
Added for Enhancement #6429 for Release 3.4.0
*/
function uploadDocument(formaction) {
	pageGreyOut();
	document.awardDocform.action = formaction
			+ '&submit_action=addAwardDocument';
}
