/**
 * This file handles the functionality of
 * add existing documents from Document vault.
 */
function onReady() {
	// This will execute when Cancel button is clicked during file upload
	$(".alert-box-addDocumentFromVault").find('#cancelAddDoc').unbind("click")
			.click(function() {
				$(".overlay").closeOverlay();
				return false;
			});

	/**
	 * This method Perform the check that the radio button is clicked Updated
	 * Method in R4
	 */
	$(".alert-box-addDocumentFromVault")
			.find('#selectdoc')
			.click(
					function() {// bind click event to link
						if ($(":radio:checked").size() > 0) {
							pageGreyOut();
							var workflowId = $("#workflowId").val();
							if (null != workflowId && '' != workflowId) {
								addFromValutForConfigAward();
							} else {
								addSelectedDocument();
							}
						} else {
						// Start Updated in R5
							$(".addDocmessagediv")
									.html('At least one radio button should be checked'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('addDocmessagediv', this)\" />");
							// End Updated in R5
							$(".addDocmessagediv").addClass('failed');
							$(".addDocmessagediv").show();
						}
					});
}
/**
 *  This method creates the url for the pagenumber
 *  */
function paging(pageNumber) {
	// Start Added in R5
	var _uploadingDocType=$('#uploadingDocumentTypeAdd').val();
	var url,idToReplace;
	if(($('#leftTreeVault').is(':visible')))
		{
			url = $("#getFolderStructureUrl").val()+"&action=enhanceddocumentvault&render_jsp_name=addDocumentFromVault&selectVault=true&isFilter=false&uploadingDocumentType="+_uploadingDocType+"&docType="+$('#addDocType').val()+"&selectDocForRelease=Solicitation&nextPage="
			+ pageNumber;
			idToReplace='folderStructure';
		}
	else
		{
			idToReplace='reRenderId';
			url = $("#addRfpDocumentResourcePopup").val() + "&action=rfpRelease&nextPageParam="
			+ pageNumber;
		}
	// End Added in R5
	hhsAjaxRender(null, document.selectRFPDocForm, idToReplace, url);
}
/**
 * This method will be called when user select any of the radio button on add
 * document screen Updated Method in R4
 */
function setHiddenParams(docType, docCategory, lastModifiedBy, modifiedDate,
		createdDate, createdBy, docTitle, docId) {
	$("#addDocType").val(docType);
	$("#docCategory").val(docCategory);
	$("#lastModifiedBy").val(lastModifiedBy);
	$("#lastModifiedDate").val(modifiedDate);
	$("#creationDate").val(createdDate);
	$("#submissionBy").val(createdBy);
	$("#docTitle").val(docTitle);
	$("#docId").val(docId);
}
/**
 *  This function is used to select the document from the list of the documents
 *  */
function addSelectedDocument() {
	// Start Updated in R5
	document.selectRFPDocForm.action =$("#addRfpDocumentUrl").val()+ "&submit_action=addDocumentFromVault";
    document.selectRFPDocForm.submit();
    // End Updated in R5
}

/**
 * This function is used to upload document by clicking Upload Document button
 * or by selecting Upload Document option from action drop down Updated Method
 * in R4
 */
function addFromValutForConfigAward() {
// Start Updated in R5
	var url= $("#addRfpDocumentUrl").val()+ "&submit_action=addDocumentFromVault";
	jQuery.ajax({
		url : url,
		type : "POST",
		success : function(response) {
			hhsAjaxRender(null, document.selectRFPDocForm, 'attachDocuments',
					url, "setErrorMessage");
// End Updated in R5
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/**
 * This method sets the error message Updated Method in R4
 */
function setErrorMessage() {
	removePageGreyOut();
	$(".overlay").closeOverlay();
}

//Added in R5
/**
 * This method is used to show document vault tree
 */
function dvTreeShow() {
    var selectedVal = $('#viewSelection').val();
    var url;
	var _uploadingDocType=$('#uploadingDocumentTypeAdd').val();
	pageGreyOut();
    if (selectedVal == "View all documents") {
    	
    	if(_uploadingDocType == "Proposal" ){
    	 url= $("#addProposalDocumentResource").val()+"&docType="+$('#addDocType').val()+"&hiddendocRefSeqNo="+$('hiddendocRefSeqNo').val()+
    		"&uploadingDocumentType=Proposal&procurementId="+$('procurementId').val()+"&selectVault=true&selectAll=true";
    	}
    	else if (_uploadingDocType == "Award"){
    		url= $("#addAwardDocumentResource").val()+"&docType="+$('#addDocType').val()+"&hiddendocRefSeqNo="+$('hiddendocRefSeqNo').val()+
    		"&uploadingDocumentType=Proposal&procurementId="+$('procurementId').val()+"&selectVault=true&selectAll=true";
    	}
    	// added for defect 7497
		else if (_uploadingDocType == "ConfigureAwardScreen") {
			var url = $("#hiddenaddDocumentFromVaultUrl").val()
					+ "&selectVault=true&selectAll=true";
		}
    	// added for defect 7497
    	else{
    		  url = $("#addRfpDocumentResource").val()+"&selectVault=true&selectAll=true";
    	}
    	var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
				if (response != null || response != '') {
					$('#folderMain').hide();
					$('#docView').show();
					var $response = $(response);
					var data = $response.contents();
					if (data != null || data != '') {
						$("#selectRFPDocForm .tabularWrapper").html(
								data.find(".tabularWrapper").html());
					}
					$("#selectdoc").show();
					removePageGreyOut();
				}
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});

	} else {
		pageGreyOut();
		var url = $("#getFolderStructureUrl").val()+ "&action=enhanceddocumentvault&render_jsp_name=addDocumentFromVault&selectVault=true&isFilter=true&uploadingDocumentType="+_uploadingDocType+"&docType="+$('#addDocType').val()+"&selectDocForRelease=Solicitation";
		var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
				$('#docView').hide();
				$('#folderMain').show();
				if (response != null || response != '') {
					var data = $(response);
					if (data != null || data != '') {
						$("#selectRFPDocForm .tWForLink").html(
								data.find(".tWForLink").html());
					$js('#leftTreeVault').jstree("destroy");
					tree($('#hdnopenTreeAjaxVar').val()+"&action=enhanceddocumentvault", 'leftTreeVault','customfolderid', "DocumentVault" ,'','');
					}
					if($(".blankDocumentList").size()>0){
						$("#selectdoc").hide();
					}else{
						$("#selectdoc").show();
					}
					if($("#isDocPresent").val() == "false"){
						$(".addDocmessagedivFolder").html('No documents exist in the Document Vault');
						$(".addDocmessagedivFolder").addClass('failed');
						$(".addDocmessagedivFolder").show();
					}
					removePageGreyOut();
				}
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
}

//Added in R5
/**
 * This method is used to open folder
 */
function openFolder(folderId, folderName,divId) {
	pageGreyOut();
	var _uploadingDocType=$('#uploadingDocumentTypeAdd').val();
	document.selectRFPDocForm.action = $('#hdnopenFolder').val()+ "&action=enhanceddocumentvault&submit_action=openFolder&render_jsp_name=addDocumentFromVault&folderName="+folderName
				+ "&folderId=" + folderId+"&isFilter=true&uploadingDocumentType="+_uploadingDocType+"&docType="+$('#addDocType').val()+"&selectDocForRelease=Solicitation";
   	var options = {
		success : function(responseText, statusText, xhr) {
			removePageGreyOut();
			var data = $(responseText);
			$("#selectRFPDocForm .tWForLink").html(
					data.find(".tWForLink").html());
				$js('#leftTreeVault').jstree("select_node", folderId);
				if($(".blankDocumentList").size()>0){
					$("#selectdoc").hide();
				}else{
					$("#selectdoc").show();
				}
		}
	};
	$(document.selectRFPDocForm).ajaxSubmit(options);
	return false;
}