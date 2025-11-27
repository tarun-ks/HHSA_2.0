/* This method called when page is getting loaded and set the values
Updated Method in R4*/
$(document).ready(
		function() {
			removePageGreyOut();
			//on click of add New Proposal button submit the page 
			$("#addNewProposal").click(
					function() {
						$("#proposalSummaryForm").attr(
								"action",
								$("#proposalSummaryForm").attr("action")
										+ "&submit_action=addNewProposal");
						pageGreyOut();
						document.proposalSummaryForm.submit();
					});
			//on click of Evaluation Scores open overlay 
			$("#EvaluationScoresId").click(function() {
				fillAndShowOverlay();
			});
			//on click of do Not Cancel Proposal close overlay
			$("#doNotCancelProposal").click(function(e) {
				overlayLaunched.closeOverlay(e);
			});
			//on click of exit button, close overlay
			$(".cancelProposal-exit").click(function(e) {
				if (overlayLaunched != null) {
					e.stopPropagation();
					overlayLaunched.closeOverlay(e);
					resetEpinOverLayVals();
				}
			});
			//on click of yes Cancel Proposal button, submit form.
			$("#yesCancelProposal").click(function(e) {
				$("#proposalSummaryForm").attr("action", $("#cancelURL").val())
				$("#proposalSummaryForm").submit();
			});

			//retract proposal
			//on click of do Not retract Proposal close overlay
			$("#doNotRetractProposal").click(function(e) {
				overlayLaunched.closeOverlay(e);
			});
			//on click of exit button, close overlay
			$(".retractProposal-exit").click(function(e) {
				if (overlayLaunched != null) {
					e.stopPropagation();
					overlayLaunched.closeOverlay(e);
					resetEpinOverLayVals();
				}
			});
			//on click of yes retract Proposal button, submit form.
			$("#yesRetractProposal").click(
					function(e) {
						$("#proposalSummaryForm").attr("action",
								$("#retractURL").val())
						$("#proposalSummaryForm").submit();
					});

			$(".evalScoreClass").each(function() {
				$(this).html(parseFloat($(this).html()).toFixed(2));
			});
		});

/* on change of action drop down from the page this function will be called
and redirect to the appropriate controller depending on the action selected. 
Updated Method in R4*/
function processAction(obj, proposalId) {
	var action = $(obj).val();
	obj.selectedIndex = "";
	if (action == 'Edit Proposal Details') {
		pageGreyOut();
		$("#proposalSummaryForm").attr("action",
				$("#editProposal").val() + "&proposalId=" + proposalId);
		//submitting the page and redirecting to proposal details page
		document.proposalSummaryForm.submit();
	} else if (action == 'Upload Proposal Documents') {
		pageGreyOut();
		$("#proposalSummaryForm").attr("action",
				$("#uploadDocument").val() + "&proposalId=" + proposalId);
		//submitting the page and redirecting to proposal documents page
		document.proposalSummaryForm.submit();
	} else if (action == 'Submit Proposal') {
		pageGreyOut();
		$("#proposalSummaryForm").attr("action",
				$("#submitProposal").val() + "&proposalId=" + proposalId);
		//submitting the page and redirecting to submit proposal page
		document.proposalSummaryForm.submit();
	}
	//performing ajax call to cancel the proposal
	else if (action == 'Cancel Proposal') {
		pageGreyOut();
		var urlAppender = $("#hiddenCancelProposal").val() + "&proposalId="
				+ proposalId;
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			success : function(e) {
				removePageGreyOut();
				$("#requestCancelProposal").html(e);
				$(".overlay").launchOverlay($(".alert-box-cancelProposal"),
						$(".cancel-Proposal"), "600px", null);
				$("a.cancel-Proposal").click(function() {
					cancelOverlay();
				});
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	} else if (action == 'View Proposal Details') {
		pageGreyOut();
		$("#proposalSummaryForm").attr(
				"action",
				$("#editProposal").val() + "&proposalId=" + proposalId
						+ "&readOnlySection=true");
		//submitting the page and redirecting to proposal details page in readonly mode
		document.proposalSummaryForm.submit();
	} else if (action == 'View Proposal Documents') {
		pageGreyOut();
		$("#proposalSummaryForm").attr(
				"action",
				$("#uploadDocument").val() + "&proposalId=" + proposalId
						+ "&readOnlySection=true");
		//submitting the page and redirecting to proposal documents page in readonly mode
		document.proposalSummaryForm.submit();
	}
	//performing ajax call to retract the proposal
	else if (action == 'Retract Proposal') {
		pageGreyOut();
		var urlAppender = $("#hiddenRetractProposal").val() + "&proposalId="
				+ proposalId;
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			success : function(e) {
				removePageGreyOut();
				$("#requestRetractProposal").html(e);
				$(".overlay").launchOverlay($(".alert-box-retractProposal"),
						$(".retract-Proposal"), "600px", null);
				$("a.retract-Proposal").click(function() {
					cancelRetractOverlay();
				});
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	} else if (action == 'View Evaluation Score') {
		viewEvaluationScores(proposalId);
	}
}

//This will execute when any Column header on grid is clicked for sorting
function sort(columnName) {
	pageGreyOut();
	$("#proposalSummaryForm")
			.attr(
					"action",
					$("#proposalSummaryForm").attr("action")
							+ "&submit_action=sortProposal&sortGridName=proposalSummary"
							+ sortConfig(columnName));
	document.proposalSummaryForm.submit();
}

//to clear and close the overlay
function clearAndCloseOverLay() {
	$("#overlayDivId").html("");
	$(".overlay").closeOverlay();
}

//on click of proposal title hyperlink, to view the proposal details
function viewProposalSummary(proposalId) {
	pageGreyOut();
	$("#proposalSummaryForm").attr("action",
			$("#editProposal").val() + "&proposalId=" + proposalId);
	document.proposalSummaryForm.submit();
}

/* on click of evaluation score hyperlink, to view the evaluation score			
Updated Method in R4*/
function viewEvaluationScores(proposalId) {
	var v_parameter = "&proposalId=" + proposalId;
	var url = $("#fetchProviderEvaluationScoresResourceUrl").val()
			+ v_parameter + "&removeMenu=asdas";
	window.open(url, 'windowOpenTab',
			'scrollbars=1,resizable=1,width=1000,height=580,left=0,top=0');
}

/* on click of status hyperlink, to view the evaluation score			
Updated Method in R4*/
function viewSelectionDetails(proposalId, evaluationPoolMappingId) {
	pageGreyOut();
	$("#proposalSummaryForm").attr(
			"action",
			$("#selectionDetailsURL").val() + "&proposalId=" + proposalId
					+ "&evaluationPoolMappingId=" + evaluationPoolMappingId);
	//submitting the page and redirecting to proposal documents page in readonly mode
	document.proposalSummaryForm.submit();
}

//function used in pagination
function paging(pageNumber) {
	$("#nextPage").val(pageNumber);
	$("#proposalSummaryForm").attr("action", $("#pagingURL").val());
	pageGreyOut();
	document.proposalSummaryForm.submit();
}