/**
 * This file is added in R6 for Return Payment
 */

/**
 * This mouseup function is added in R6 for Return Payment 
 * for hiding a div on clicking outside of it
 */
$(document).mouseup(function (e)
{
    var container = $('#SampleNotificationDiv');

    if (!container.is(e.target) // if the target of the click isn't the container...
        && container.has(e.target).length === 0) // ... nor a descendant of the container
    {
        container.hide();
    }
});

$(document).mouseup(function (e)
		{
		    var container = $('#NotificationDiv');

		    if (!container.is(e.target) // if the target of the click isn't the container...
		        && container.has(e.target).length === 0) // ... nor a descendant of the container
		    {
		        container.hide();
		    }
		});
/**
 * End
 */


/**
 * This function is used to launch add return payment overlay
 */
function returnedPaymentConfirm() {
	$(".errorDivForContractBudget").hide();
	var urlAppender = $("#launchReturnedPaymentOverlay").val();
	var v_parameter = "&budgetID=" + budgetID + "&contractID=" + contractID;
	jQuery
			.ajax({
				type : "POST",
				url : urlAppender,
				data : v_parameter,
				success : function(responseText) {
					//emergency build 6.0.1 - INC000001386100/INC000001385777
					//Fix for Budget page not loading due to single quote in contract title
					$("#addReturnedPaymentformData").html(
							$(responseText).find("#addReturnedPaymentformData").html());
					$('#fiscalYear').text(fiscalYearID);
					$(".overlay").launchOverlay($(".alert-box-amend-contract"),
							$(".exit-panel"), "620px", "auto", "loadOnReady");
					$("#notifyProvider").hide();
					$("#notifyProviderList").hide();
					//Change for Defect 8603
					$('#checkAmountVal,#descriptionInput,#agencyTrackingNumber')
							.val('');
					//Change for Defect 8603
					$('.error').text('');
					$('#checkReceivedRadio:checked,#notifyProviderVal:checked')
							.prop('checked', false);
					enableDictionary();
					addHighlightOnTextarea($("#descriptionInput"));
				}
			});

}

/**
 * This function will clear the existing text on overlay
 * and reset fields
 */
function clearAndCloseOverLay() {
	$("#overlayDivId").html("");
	$('#checkReceivedRadio:checked').prop('checked', false);
	$(".overlay").closeOverlay();
	$(".overlay").hide();
}

/**
 * This function is used to add return payment
 */
function addReturnedPayment() {
	var v_parameter = "&budgetID=" + budgetID + "&checkAmountVal="
			+ $('#checkAmountVal').val() + "&descriptionInput="
			+ $('#descriptionInput').val() + "&checkReceivedRadio="
			+ $("input[name='checkReceivedRadio']:checked").val()
			+ "&notifyProviderVal="
			+ $("input[name='notifyProviderVal']:checked").val()
			+ "&contractID=" + contractID;
	var urlAppender = $("#addReturnPaymentUrl").val();
	if (($('#checkReceivedRadio:checked').val() == 'Y' || $(
			'#checkReceivedRadio:checked').val() == 'N')
			|| ($('#checkAmountVal').val() != '' || $('#checkAmountVal').val() != null)
			|| ($('#descriptionInput').val() != '' || $('#descriptionInput')
					.val() != null)) {
		if (($('#checkReceivedRadio:checked').val() == 'Y')
				|| ($('#notifyProviderVal:checked').val() == 'Y' || $(
						'#notifyProviderVal:checked').val() == 'N')) {
			pageGreyOut();
			jQuery.ajax({
				type : "POST",
				url : urlAppender,
				data : v_parameter,
				success : function(e) {
					$(document.addReturnedPaymentForm).ajaxSubmit(options);
					clearAndCloseOverLay();
					showCBGridTabsJSP('returnedPayment',
							'returnedPaymentWrapper', '', '');
					removePageGreyOut();
				},
				error : function(data, textStatus, errorThrown) {
					removePageGreyOut();
				}
			});
		} else {
			$('#notifyProviderVal').parent().find('.error').show();
		}

	} else {
		$('#checkReceivedRadio').parent().find('.error').show();
	}
}

/**
 * This function will hide and display the notify provider option when
 * option of check received is selected
 * @param selected
 */
function showProviderRadio(selected) {
	if (selected.value == 'N') {
		$("input[name='notifyProviderVal']:checked").prop('checked', false);
		$("#notifyProvider").show();
	} else {
		$("input[name='checkReceivedRadio']:checked").val('Y');
		$("#notifyProvider").hide();
		$("input[name='notifyProviderVal']:checked").prop('checked', false);
		$('select').prop('selectedIndex', 0);
		$("#notifyProviderList").hide();
	}
}
/**
 * This function is used to show provider list
 * @param selected
 */
function showProviderList(selected) {
	$('select').prop('selectedIndex', 0);
	if (selected.value == 'Y') {
		$("#notifyProviderList").show();
	} else {
		$("#notifyProviderList").hide();
	}
}
/**
 * This function will clear the existing fields on the overlay
 * and when close button is clicked
 */
function clearAndCloseOverLayReturnPayment() {
	$('.error').text('');
	$("#overlayDivId").html("");
	$("input[name='notifyProviderVal']:checked").prop('checked', false);
	$("input[name='checkReceivedRadio']:checked").prop('checked', false);
	$('select').prop('selectedIndex', 0);
	$(".overlay").closeOverlay();
	$(".overlay").hide();
}
/**
 * This function will clear error span
 */
function clearErrorSpan() {
	$('.error').text('');
}
/**
 * This function is called when Option is selected
 * from dropdown to initiate , cancel or view details of return payment
 * @param selected
 */
function onSelectChange(selected) {
	$(".errorDivForContractBudget").hide();
	var values = selected.value.split('-');
	selected.selectedIndex = 0;
	if (values[0].indexOf("View") != -1) {
		var url = $("#hiddenViewReturnedPayment").val() + "&budgetId="
				+ budgetID + '&returnedPaymentId=' + values[1];
		window.open(url, 'windowOpenTab',
				'scrollbars=1,resizable=1,width=980,height=704,left=0,top=0');
	} else if (values[0].indexOf("Cancel") != -1) {
		cancelReturnedPaymentOverlay(values[1]);
	} else if (values[0].indexOf("Initiate") != -1) {
		var success_flag = updateReturnedPaymentStatus($("#initiateReturnedPayment").val(),
				values[1]);
	}
}
/**
 * This function is used to update return payment status
 * @param url
 * @param returnedPaymentId
 */
function updateReturnedPaymentStatus(url, returnedPaymentId) {
	var v_parameter = '&returnedPaymentId=' + returnedPaymentId + "&budgetID="
			+ budgetID + "&contractID=" + contractID;
	var urlAppender = url;
	pageGreyOut();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			clearAndCloseOverLay();
			showCBGridTabsJSP('returnedPayment', 'returnedPaymentWrapper', '',
					'');
			removePageGreyOut();
			if(e.error==1){
				var _message= e.message;
		          $(".errorDivForContractBudget").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDivForContractBudget', this)\" />");
		          $(".errorDivForContractBudget").removeClass("passed");
		          $(".errorDivForContractBudget").addClass("failed");
		          $(".errorDivForContractBudget").show();
		          return "error";
			}
			else if(e.error==2){
				var _message= e.message;
		        $(".errorDivForContractBudget").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDivForContractBudget', this)\" />");
		        $(".errorDivForContractBudget").removeClass("failed");
		        $(".errorDivForContractBudget").addClass("passed");
		        $(".errorDivForContractBudget").show();
		        return "success";
			}
		},
		error : function(data, textStatus, errorThrown) {
			clearAndCloseOverLay();
			showCBGridTabsJSP('returnedPayment', 'returnedPaymentWrapper', '',
					'');
			removePageGreyOut();
			var _message= "This request could not be completed. Please try again in a few minutes.";
	          $(".errorDivForContractBudget").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDivForContractBudget', this)\" />");
	          $(".errorDivForContractBudget").removeClass("passed");
	          $(".errorDivForContractBudget").addClass("failed");
	          $(".errorDivForContractBudget").show();
	          return "error";
		}
	});
}

/**
 * This Function is used to get Notification History For Budget
 */
function getNotificationHistory() {
	$(".errorDivForContractBudget").hide();
	pageGreyOut();
	$.ajax({

		type : "POST",
		url : $('#getNotificationHistoryUrl').val(),
		data : {
			budgetId : budgetID
		},
		success : function(responseText, data) {
			removePageGreyOut();
			$(".alert-box-GetNotificationHistory").html(
					$(responseText).filter(".alert-box-GetNotificationHistory")
							.html());
			$(".overlay").launchOverlay($(".alert-box-GetNotificationHistory"),
					$(".exit-panel, #linkedCancelButton"), "560px", "380px");
			$("#notificationDataTable").scrollableTable(200);
			$("#notificationDataTable").width("480px");
		}
	});

}
/**
 * This Function is used to notify a respective provider
 * regarding the balance of unrecovered funds on the <FY>
 * budget
 */
function notifyProvider() {
	$(".errorDivForContractBudget").hide();
	pageGreyOut();
	$('#sendNotButton').prop('disabled', true);
	$.ajax({

		type : "POST",
		url : $('#getNotificationProvider').val(),
		success : function(responseText) {
			$("#notProviderSelection").html(
					$(responseText).find("#notProviderSelection").html());
			$("#NotificationText").html(
					$(responseText).find("#NotificationText").html());
			$(".overlay").launchOverlay($(".alert-box-Notify-Provider"),
					$(".exit-panel"), "450px", "275px", null);
			removePageGreyOut();
		}
	});
}

/**
 * This Function is used to disable and enable send notification button
 */
function disableButton() {

	if ($('#notProviderSelection').val() == "") {
		$('#sendNotButton').prop('disabled', true);
	} else {
		$('#sendNotButton').prop('disabled', false);
	}
}
/**
 * This function is used to send notification to Providers for 
 * budgets that have a balance of unrecovered funds 
 */
function sendNotificationForm() {
	pageGreyOut();
	var v_parameter = "actionSelected=" + "notifyProvider"
			+ "&programName=" + $("#programName").val() + "&fiscalYear="
			+ fiscalYearID + "&targetUser=" + $("#notProviderSelection").val() + "&budgetID="
			+ budgetID + "&programId=" + $("#programId").val();
	var urlAppender = $("#sendNotificationAlert").val();
	$('#bulkNotificationSubmit').prop('disabled', true);
	jQuery
			.ajax({
				type : "POST",
				url : urlAppender,
				data : v_parameter,
				success : function(e) {
					if (e == "notification sent") {
						clearAndCloseOverLay();
						showCBGridTabsJSP('returnedPayment',
								'returnedPaymentWrapper', '', '');
						removePageGreyOut();
						var _message = "Notification has been sent successfully.";
						$(".errorDivForContractBudget")
								.html(
										_message
												+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDivForContractBudget', this)\" />");
						$(".errorDivForContractBudget").removeClass("failed");
						$(".errorDivForContractBudget").addClass("passed");
						$(".errorDivForContractBudget").show();
					} else if (e == "notification not sent") {
						clearAndCloseOverLay();
						showCBGridTabsJSP('returnedPayment',
								'returnedPaymentWrapper', '', '');
						removePageGreyOut();
						var _message = "Notification has not been  sent successfully.";
						$(".errorDivForContractBudget")
								.html(
										_message
												+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDivForContractBudget', this)\" />");
						$(".errorDivForContractBudget").addClass("failed");
						$(".errorDivForContractBudget").show();
					}
				},
				error : function() {
					clearAndCloseOverLay();
					showCBGridTabsJSP('returnedPayment',
							'returnedPaymentWrapper', '', '');
					removePageGreyOut();
					var _message= "This request could not be completed. Please try again in a few minutes.";
			          $(".errorDivForContractBudget").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDivForContractBudget', this)\" />");
			          $(".errorDivForContractBudget").removeClass("passed");
			          $(".errorDivForContractBudget").addClass("failed");
			          $(".errorDivForContractBudget").show();
				}
			});
}

/**
 * This function is used to launch cancel return payment overlay
 * @param returnedPaymentId
 */
function cancelReturnedPaymentOverlay(returnedPaymentId) {
	pageGreyOut();
	$("#returnedPaymentId").val(returnedPaymentId);
	$(".overlay").launchOverlay($(".alert-box-Cancel-ReturnPayment"),
			$(".exit-panel"), "400px", "auto", null);
	removePageGreyOut();
	$("#cancelPayment").click(
			function() {
				var success_flag = updateReturnedPaymentStatus($("#cancelReturnedPayment").val(),
						$("#returnedPaymentId").val());
			});
	
}
/**
 * This function is called when cancel return payment is clicked
 */
function cancelReturnedPayment() {
	updateReturnedPaymentStatus($("#cancelReturnedPayment").val(),
			$("#returnedPaymentId").val());
}
/**
 * This function is added to add properties for a div
 * for sample notification in add return payment overlay
 */
function viewNotificationSample(){	
	$('#SampleNotificationDiv').css("display","block");
	$('#SampleNotificationDiv').css("visibility","inherit");
}
/**
 * This function is added to add properties for a div 
 * for sample notification in notify provider overlay
 */
function viewNotificationSampleNotify(){
	$('#NotificationDiv').css("display","block");
	$('#NotificationDiv').css("visibility","inherit");	
}
// End : Added in R6
//Fix for QC defect : 8611, to show 'X' on sample notification pop up
function closeSampleNotificationDiv()
{
	$('#SampleNotificationDiv').hide();
}
//Fix for QC defect : 8611, to show 'X' on sample notification pop up
function closeNotificationDiv()
{
	$('#NotificationDiv').hide();
}
