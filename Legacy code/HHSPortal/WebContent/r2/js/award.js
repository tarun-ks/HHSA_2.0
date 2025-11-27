/**
 *  This method is invoked on selecting an option from the action dropdown on awards screen.
*	Updated Method in R4
*/
function selectOption(obj, procurementId, evaluationGroupId, organizationId, contractNumber,
		isFinancial, contractId, procStatus, isOpenEndedRFP, contractTypeId, evaluationPoolMappingId, epin) {
	if ($(obj).val() == 1) {
		//call method for assign e-pin
		assignAwardEPIN(this, procurementId, contractId, contractTypeId, isFinancial, isOpenEndedRFP, evaluationGroupId, evaluationPoolMappingId);
	} else if ($(obj).val() == 2) {
		viewAptProgress(obj, epin);
	} else if ($(obj).val() == 3) {
		var url = $("#hiddenViewAwardDocuments").val() + "&procurementId="
				+ procurementId + "&evaluationPoolMappingId=" + evaluationPoolMappingId 
				+ "&organizationId=" + organizationId
				+ "&isFinancials=" + isFinancial + "&asProcStatus="
				+ procStatus + "&contractId=" + contractId +"&removeMenu=asdas";
		window.open(url, 'windowOpenTab',
				'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
	} else if ($(obj).val() == 4) {
		//method for cancel award
		cancelAward(this, procurementId, contractId, organizationId, procStatus, evaluationPoolMappingId);
	}
	document.getElementById($(obj).attr("id")).selectedIndex = "";
}
/**
 *  This method call on page load
* 	Updated Method in R4
* */
$(document).ready(
		function() {
			
			// on click of return to awards and contracts summary button
			$("#returnAwardContractSummary")
					.click(
							function() {
								$("#navigationForm").find("#forAction")
										.eq(0).val("awardContract");
								$("#navigationForm").find("#topLevelFromRequest")
								.eq(0).val("AwardsandContracts");
								$("#navigationForm").find("#render_action").eq(0)
								.val("awardsAndContracts");
								$("#navigationForm").find("#ES").eq(0)
										.val("0");
								pageGreyOut();
								document.navigationForm.submit();
							});
			
			// on changing competition pool from drop down
			$("#compPoolDropDown").change(
					function() {
						if($(this).val()!=-1 && $(this).val()!=$("#navigationForm").find("#evaluationPoolMappingId").eq(0).val()){
							$("#navigationForm").find("#forAction").eq(
									0).val("awardContract");
							$("#navigationForm").find("#topLevelFromRequest").eq(0).val(
								"AwardsandContracts");
							$("#navigationForm").find("#midLevelFromRequest").eq(0).val(
								"AwardsandContractsScreen");
							$("#navigationForm").find("#ES").eq(0).val(
									"0");
							$("#navigationForm").find("#render_action")
									.eq(0).val("awardsAndContracts");
							$("#navigationForm").find(
									"#evaluationPoolMappingId").eq(0)
									.val($(this).val());
							pageGreyOut();
							document.navigationForm.submit();
						}
					});


			if ($("#screenLockedFlag").val() == "false") {
				var ignoreList = [ "I need to...", "View Award Documents",
						"View APT Progress" ];
				$("select[id^=actions] option").filter(function() {
					return $.inArray($(this).text(), ignoreList) < 0;
				}).remove();
				$("select[id^=actions]").each(function() {
					if ($(this).find("option").size() > 1) {
						$("select[id^=actions]").attr("disabled", false);
					}
				});
			}
// Start Updated in R5
			$(".tableAwardAmount").autoNumeric('init', {
// End Updated in R5
				vMax : '9999999999999999',
				vMin : '0.00'
			});
			$(".exit-panel").click(function() {
				$(".alert-box").hide();
				$(".overlay").hide();
			});
			$("#awardAmt1").autoNumeric('init', {
				vMax : '9999999999999999',
				vMin : '0.00'
			});
			//Changes in R5 starts
			$('#pendingAwardTipDiv').hide();
			$('.tabularWrapper table').find('.tableAwardAmount').css({'float':'none'});
			if($(".red-ex-mark").size()>1)
				{
				$('#pendingAwardTipDiv').show();
				}
			$("#btnSubmitCB").attr("class", "graybtutton");
			$("#btnSubmitCB").attr("disabled", "true");
			$('#usernameDiv.row').hide();
			$('#passwordDiv.row').hide();
			$('#chkSubmitCBForm').change(function showHideButton(){
				if($('#chkSubmitCBForm').prop('checked')){
					$('#usernameDiv.row').show();
					$('#passwordDiv.row').show();
					$("#btnSubmitCB").attr("class", "redbtutton");
					$("#btnSubmitCB").removeAttr("disabled");
				}else{
					$('#usernameDiv.row').hide();
					$('#passwordDiv.row').hide();
					$("#btnSubmitCB").attr("class", "graybtutton");
					$("#btnSubmitCB").attr("disabled", "true");
				}
			});
			//Changes in R5 ends
		});

/**
 *  function used to open the Cancel Award popup.
* 	Updated Method in R4
* */
function cancelAward(selectElement, procurementId, contractId, organizationId,
		procStatus, evaluationPoolMappingId) {
	pageGreyOut();
	var v_parameter = "&contractID=" + contractId + "&organizationId="
			+ organizationId + "&procurementId=" + procurementId
			+ "&procurementStatus=" + procStatus + "&evaluationPoolMappingId=" + evaluationPoolMappingId;
	var urlAppender = $("#hiddenCancelAwardOverlayUrl").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			removePageGreyOut();
			$("#requestCancel").html(e);
			$(".overlay").launchOverlay($(".alert-box-cancelAward"),
					$(".cancel-Award"), "600px", null);
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/**
 *  function used to open the Assign Award E-pin popup.
*	Updated Method in R4
**/
function assignAwardEPIN(selectElement, procurementId, contractId,
		contractTypeId, isFinancial, isOpenEndedRFP, evaluationGroupId, evaluationPoolMappingId) {
	pageGreyOut();
	var urlAppender = $("#hiddenAssignAwardEpinOverlayContentUrl").val()
			+ "&contractID=" + contractId + "&contractTypeId=" + contractTypeId + "&isFinancials=" + isFinancial + "&isOpenEndedProc="
			+ isOpenEndedRFP + "&evaluationPoolMappingId="
			+ evaluationPoolMappingId + "&evaluationGroupId="
			+ evaluationGroupId;
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {
			removePageGreyOut();
			$("#assignAwardPIN").html(e);
			$(".overlay").launchOverlay($(".alert-box-assignAwardPIN"),
					$(".cancel-assignAwardPIN"), "650px", null);

		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	});
}
/**
 *  function used to open the View APT Progress popup.
 **/
function viewAptProgress(selectElement, epin) {
	pageGreyOut();

	var urlAppender = $("#viewAptInformation").val() + "&AwardEPin=" + epin;
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		success : function(e) {

			removePageGreyOut();
			$("#viewAptProgress").html(e);
			$(".overlay").launchOverlay($(".alert-box-viewAptProgress"),
					$(".viewAptProgress"), "600px", null);
		},
		error : function(data, textStatus, errorThrown) {
			removePageGreyOut();
		}
	});
}
/**
 * function used in pagination
 **/
function paging(pageNumber) {
	$("#nextPage").val(pageNumber);
	pageGreyOut();
	document.awardAndContracts.submit();
}
/**
 *  Added in R5
 * */
function cancelAllAwards() {
	$(".overlay").launchOverlay($(".alert-box-cancelAllAwards"), $(".exit-panel"), "600px", null);
}
/**
 *  This function is called when finish button is clicked.
*/
function finishTask() {
	var isValidUsername = $.trim($('#txtSubmitCBUserName').val());
	var isValidPassword = $.trim($('#txtSubmitCBPassword').val());
	if( isValidUsername == "" || isValidPassword == ""){
		if (isValidUsername == "") {
			$("#usernamespan").text("! This field is required");
		}else{ 
			$("#usernamespan").text(""); 
		}
		if (isValidPassword == "") {
			$("#passwordspan").text("! This field is required");
		} else{ 
			$("#passwordspan").text(""); 
		}
    } else  {
		$("#awardAndContracts").attr("action", $("#hiddenCancelAllAwardUrl").val());
		$(document.awardAndContracts).ajaxSubmit(options);
		pageGreyOut();
		// Defect 7219 changes
	}
}
var options = 
{	
	success: function(responseText, statusText, xhr ) 
	{
	 	var $response=$(responseText);
        var data = $response.contents().find("#errorPlacement");
        if(data.size() > 0){
        	$("#errorPlacementWrapper").html(data);
        	$("#errorPlacementWrapper").show();
        	removePageGreyOut();
		        } else {
			$("#overlay").closeOverlay();
			pageGreyOut();
			$("#action_redirect").val("true");
			$("#awardAndContracts").attr(
					"action",
					$("#redirectURL").val() + "&refreshOnCancelAll=yes"
							+ "&competitionPoolTitle="
							+ $('#competitionPoolTitle').val());
			document.awardAndContracts.submit();
		}
	},
	error:function (xhr, ajaxOptions, thrownError)
	{                     
		showErrorMessagePopup();
		removePageGreyOut();
	}			
}
/**
 *  This function is used to close overlay
 * */
function cancelOverLay() {
	$(".overlay").closeOverlay();
}
