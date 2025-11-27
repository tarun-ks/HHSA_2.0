// This method is used to sort the Contract Amendment
function sort(columnName) {
	document.contractFilterFormAmendment.reset();
	$("#contractFilterFormAmendment")
			.attr(
					"action",
					$("#contractFilterFormAmendment").attr("action")
							+ "&next_action=sortAmendContractList&sortGridName=amendContractListMap"
							+ sortConfig(columnName));
	document.contractFilterFormAmendment.submit();
}

//This method is used to provide the pagination for Amendment contract.
function paging(pageNumber) {
	document.contractFilterFormAmendment.reset();
	$("#contractFilterFormAmendment").attr(
			"action",
			$("#contractFilterFormAmendment").attr("action")
					+ "&next_action=fetchNextAmendContracts&nextPage=" + pageNumber);
	document.contractFilterFormAmendment.submit();
}

//Javascript for filter popup
//release 3.14.0
function setVisibilityAmendment(id, visibility) {
	callBackInWindow("closePopUp");
    if ($("#" + id).is(":visible")) {
          document.contractFilterFormAmendment.reset();
    }
	$("#" + id).toggle();
	disableProgramDropDown();
}

//Set the filter to default values
function settoDefaultFilters() {
	$('input:text').val('');
	$('select').find('option:first').attr('selected', 'selected');
	$('input:checkbox').attr('checked', true);
	$("span.error").empty();
	$("#chkSuspended").attr('checked', false);
	$("#chkCancelled").attr('checked', false);
}



//This method executes on click of download document button
function downloadAmendmentDocumentUrl(){
		pageGreyOut();
		var v_parameter = "";
		var urlAppender = $("#downloadAmendmentDocumentUrl").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(data) {
				removePageGreyOut();
				var dataJSON = $
						.parseJSON(data);
				if (dataJSON != null
						&& dataJSON.output != null) {
					if (dataJSON.output[0].error != null) {
						$(
								"#jsmessagediv")
								.html(
										dataJSON.output[0].error);
						$(
								"#jsmessagediv")
								.show();
					} else {
						var filePath = dataJSON.output[0].path;
						window.location.href = ($(
								"#contextPathSession")
								.val()
								+ "/dbdDoc/" + filePath);
						$(".overlay").closeOverlay();
						var delay=5000;//5 seconds
					    setTimeout(function(){
					    	window.location.href = $("#duplicateRenderAmendment").val();
					    },delay); 
					}
				}
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				 removePageGreyOut();
			}
		});
}